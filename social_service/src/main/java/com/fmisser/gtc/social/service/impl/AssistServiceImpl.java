package com.fmisser.gtc.social.service.impl;

import com.fmisser.fpp.cache.redis.service.RedisService;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.i18n.SystemTips;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.mq.GreetDelayedBinding;
import com.fmisser.gtc.social.service.AssistService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author fmisser
 * @create 2021-05-10 下午3:41
 * @description
 */
@Slf4j
@Service
@AllArgsConstructor
public class AssistServiceImpl implements AssistService {
    private final RedisService redisService;
    private final SystemTips systemTips;
    private final GreetDelayedBinding greetDelayedBinding;

    @Override
    public void sendRechargeMsg(User user) throws ApiException {
        String key = String.format("social:assist:recharge:touser:%s", user.getDigitId());
        boolean isExist = redisService.hasKey(key);
        if (!isExist) {
            // 消息放到延迟队列

            // 第一条消息
            String sendMsgPayload = String
                    .format("3,%s,%s,%s", "", user.getId(), systemTips.recharge1Msg());
            Message<String> rechargeDelayedMessage = MessageBuilder
                    .withPayload(sendMsgPayload).setHeader("x-delay", 5 * 1000).build();
            boolean ret = greetDelayedBinding.greetDelayedOutputChannel().send(rechargeDelayedMessage);
            if (!ret) {
                log.error("send recharge msg part 1 failed!");
                return;
            }

            // 第二条消息
            sendMsgPayload = String
                    .format("3,%s,%s,%s", "", user.getId(), systemTips.recharge2Msg());
            rechargeDelayedMessage = MessageBuilder
                    .withPayload(sendMsgPayload).setHeader("x-delay", 6 * 1000).build();
            ret = greetDelayedBinding.greetDelayedOutputChannel().send(rechargeDelayedMessage);

            if (!ret) {
                log.error("send recharge msg part 2 failed!");
                return;
            }

            Date now = new Date();
            redisService.set(key, now.getTime(), 24 * 3600);
        }
    }
}
