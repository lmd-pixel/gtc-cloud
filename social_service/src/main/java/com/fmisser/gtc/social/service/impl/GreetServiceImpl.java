package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.RecommendAnchorDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.domain.Greet;
import com.fmisser.gtc.social.domain.GreetMessage;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.mq.GreetDelayedBinding;
import com.fmisser.gtc.social.repository.GreetRepository;
import com.fmisser.gtc.social.service.GreetMessageService;
import com.fmisser.gtc.social.service.GreetService;
import com.fmisser.gtc.social.service.RecommendService;
import com.fmisser.gtc.social.service.UserService;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GreetServiceImpl implements GreetService {

    private final GreetRepository greetRepository;

    private final UserService userService;

    private final GreetMessageService greetMessageService;

    private final GreetDelayedBinding greetDelayedBinding;

    private final RecommendService recommendService;

    public GreetServiceImpl(GreetRepository greetRepository,
                            UserService userService,
                            GreetMessageService greetMessageService,
                            GreetDelayedBinding greetDelayedBinding,
                            RecommendService recommendService) {
        this.greetRepository = greetRepository;
        this.userService = userService;
        this.greetMessageService = greetMessageService;
        this.greetDelayedBinding = greetDelayedBinding;
        this.recommendService = recommendService;
    }

    @Override
    public int createGreet(User user) throws ApiException {

        if (user.getIdentity() != 0) {
            return 0;
        }

        if (isTodayGreetExist(user)) {
            return 0;
        }

        // 为了避免几个阶段随机主播和随机的消息的重复，第一次就直接创建所有阶段需要的随机主播和消息
        int totalCount = 10;

        // 寻找count个主播
//        List<User> anchorList = userService.getRandAnchorList(totalCount);

        // 男用户选择女主播骚扰， 女用户选择男主播骚扰
        List<RecommendAnchorDto> anchorList = recommendService
                .getRandRecommendAnchorList(totalCount, user.getGender() == 0 ? 1 : 0);
        List<GreetMessage> greetMessageList = greetMessageService
                .getRandGreetMessage(totalCount, user.getGender() == 0 ? 1 : 0);
        List<Greet> greetList = new ArrayList<>();
        for (int i = 0; i < anchorList.size(); i++) {
//            User anchor = anchorList.get(i);
            RecommendAnchorDto anchor = anchorList.get(i);
            GreetMessage greetMessage = greetMessageList.get(i);

            Greet greet = new Greet();
            greet.setUserId(user.getId());
            greet.setAnchorId(anchor.getId());
            if (i < 5) {
                greet.setStage(1);
            } else if (i < 8) {
                greet.setStage(2);
            } else {
                greet.setStage(3);
            }
            greet.setGreetMsgId(greetMessage.getId());
            greetList.add(greet);
        }

        greetRepository.saveAll(greetList);

        return updateGreet(user, 1);
    }

    @Override
    public int updateGreet(User user, int stage) throws ApiException {

        // 延迟发送消息
        Date now = new Date();
        Date todayStartTime = DateUtils.getDayStart(now);
        Date todayEndTime = DateUtils.getDayEnd(now);

        List<Greet> greetList = greetRepository
                .findByUserIdAndCreatTimeBetweenAndStage(user.getId(), todayStartTime, todayEndTime, stage);
        List<Integer> msgIdList = greetList.stream().map(Greet::getGreetMsgId).collect(Collectors.toList());
        List<GreetMessage> greetMessageList = greetMessageService.getMessageList(msgIdList);
        for (int i = 0; i < greetList.size(); i++) {
            Greet greet = greetList.get(i);
            GreetMessage greetMessage = greetMessageList.get(i);

            // 发送消息加入队列
            String sendMsgPayload = String
                    .format("1,%s,%s,%s", greet.getAnchorId(), greet.getUserId(), greetMessage.getMessage());
            // 随机一个10-50s之间的时间
            int randomDelay = new Random().nextInt(40) + 10;
            Message<String> greetDelayedMessage = MessageBuilder
                    .withPayload(sendMsgPayload).setHeader("x-delay", randomDelay * 1000).build();
            boolean ret = greetDelayedBinding.greetDelayedOutputChannel().send(greetDelayedMessage);
            if (!ret) {
                // TODO: 2021/1/25 处理发送失败
            }
        }

        // 生成下一阶段的延迟事件
        if (stage < 3) {

            // 第一次1分钟， 第二次3分钟
            int delayQueue = stage == 1 ? 60000 : 180000;

            // 发送延时队列事件
            String delayQueuePayload = String.format("2,%s,%d,", user.getDigitId(), stage);
            Message<String> greetDelayedMessage = MessageBuilder
                    .withPayload(delayQueuePayload).setHeader("x-delay", delayQueue).build();
            boolean ret = greetDelayedBinding.greetDelayedOutputChannel().send(greetDelayedMessage);
            if (!ret) {
                // TODO: 2021/1/25 处理发送失败
            }
        }

        return 1;
    }

    @Override
    public boolean isTodayGreetExist(User user) throws ApiException {
        Date now = new Date();
        Date todayStartTime = DateUtils.getDayStart(now);
        Date todayEndTime = DateUtils.getDayEnd(now);
        // 获取当天是否已经在实行骚扰机制
        List<Greet> greetList = greetRepository.findByUserIdAndCreatTimeBetween(
                user.getId(), todayStartTime, todayEndTime);
        return greetList.size() > 0;
    }

    @Override
    public boolean isTodayReply(User user) throws ApiException {
        Date now = new Date();
        Date todayStartTime = DateUtils.getDayStart(now);
        Date todayEndTime = DateUtils.getDayEnd(now);
        // 获取当天是否已经回复主播
        List<Greet> greetList = greetRepository.findByUserIdAndCreatTimeBetweenAndReplyMsgIdNotNull(
                user.getId(), todayStartTime, todayEndTime);
        return greetList.size() > 0;
    }

    @Override
    public int userReplyToday(User userFrom, User userTo, Long replyMsgId) throws ApiException {
        Date now = new Date();
        Date todayStartTime = DateUtils.getDayStart(now);
        Date todayEndTime = DateUtils.getDayEnd(now);

        Greet greet = greetRepository.findByUserIdAndAnchorIdAndCreatTimeBetween(
                userFrom.getId(), userTo.getId(), todayStartTime, todayEndTime);

        if (Objects.nonNull(greet) && Objects.isNull(greet.getReplyMsgId())) {
            greet.setReplyMsgId(replyMsgId);
            greetRepository.save(greet);
            return 1;
        }

        return 0;
    }
}
