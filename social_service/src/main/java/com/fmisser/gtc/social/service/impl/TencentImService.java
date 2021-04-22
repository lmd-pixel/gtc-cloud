package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.im.*;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.ImConfProp;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.domain.*;
import com.fmisser.gtc.social.feign.ImFeign;
import com.fmisser.gtc.social.job.CallCalcJobScheduler;
import com.fmisser.gtc.social.mq.WxWebHookBinding;
import com.fmisser.gtc.social.repository.*;
import com.fmisser.gtc.social.service.*;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.cvm.v20170312.CvmClient;
import com.tencentcloudapi.trtc.v20190722.TrtcClient;
import com.tencentcloudapi.trtc.v20190722.models.DismissRoomRequest;
import com.tencentcloudapi.trtc.v20190722.models.DismissRoomResponse;
import lombok.Data;
import lombok.SneakyThrows;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.util.*;
import java.util.zip.Deflater;

import static com.fmisser.gtc.social.service.impl.TencentImCallbackService.createBillSerialNumber;


/**
 * 腾讯im 实现
 */

@Service
public class TencentImService implements ImService {

    @Autowired
    private ImConfProp imConfProp;

    @Autowired
    private ImFeign imFeign;

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private CallBillRepository callBillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private OssConfProp ossConfProp;

//    @Autowired
//    private UserService userService;

    @Autowired
    private WxWebHookBinding wxWebHookBinding;

    @Autowired
    private ActiveService activeService;

    @Autowired
    private CallCalcJobScheduler callCalcJobScheduler;

    @Override
    public String login(User user) throws ApiException {
        // TODO: 2021/1/7 记录用户登录

        // 返回 用户 user sign
        return genUserSign(user);
    }

    @Override
    public int logout(User user) throws ApiException {
        // TODO: 2021/1/7 记录用户登出

        return 0;
    }

    @Override
    public Long call(User userFrom, User userTo, int type) throws ApiException {

        if (userFrom.getIdentity() == 0 && userTo.getIdentity() == 0) {
            return -1L;
        }

        if (userFrom.getIdentity() == 1 && userTo.getIdentity() == 1) {
            return -1L;
        }


        // 创建通话房间
        Call call = new Call();
        call.setType(type);
//        call.setUserIdFrom(userFrom.getId());
//        call.setUserIdTo(userTo.getId());

        if (userFrom.getIdentity() == 1 && userTo.getIdentity() == 0) {
            // 主播打给用户
            call.setUserIdFrom(userTo.getId());
            call.setUserIdTo(userFrom.getId());
            call.setCallMode(1);
        } else if (userFrom.getIdentity() == 0 && userTo.getIdentity() == 1) {
            // 用户打给用户
            call.setUserIdFrom(userFrom.getId());
            call.setUserIdTo(userTo.getId());
            call.setCallMode(0);
        } else if (userFrom.getIdentity() == 1 && userTo.getIdentity() == 1) {
            // 主播打给主播
            call.setUserIdFrom(userFrom.getId());
            call.setUserIdTo(userTo.getId());
            call.setCallMode(3);
        }

        call.setRoomId(Long.valueOf(genRoomId()));
        call.setCommId(UUID.randomUUID().toString());

        call = callRepository.save(call);
        return call.getRoomId();
    }

    @Override
    public int accept(User userFrom, User userTo, Long roomId) throws ApiException {
        // TODO: 2021/1/8 校验是否匹配

//        if (userFrom.getIdentity() == 0 && userTo.getIdentity() == 0) {
//            return -1;
//        }
//
//        if (userFrom.getIdentity() == 1 && userTo.getIdentity() == 1) {
//            return -1;
//        }

        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            return -2;
        }

        // 设定开始时间
        call.setStartTime(new Date());
        callRepository.save(call);

        return 1;
    }

    @Override
    public Map<String, Object> hangup(User user, Long roomId, String version) throws ApiException {

        // 解散房间
//        trtcDismissRoom(roomId);

        // 结束不进行任何结算
        Map<String, Object> resultMap = new HashMap<>();

        // 根据用户不同返回不同类型数据
        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 0) {
            // 生成结束信息
            Date now = new Date();
            call.setFinishTime(now);
            call.setIsFinished(1);

            Date startTime = call.getStartTime();
            if (Objects.isNull(startTime)) {
                // 还没开始
//                call.setFinishTime(now);

                // 发送推送消息,告知用户已取消
                if (call.getCallMode() == 0) {
                    if (user.getId().equals(call.getUserIdFrom())) {
                        //
                        Optional<User> toUser = userRepository.findById(call.getUserIdTo());
                        String content = String.format("用户%s拨打已取消", user.getNick());
                        toUser.ifPresent(value -> sendToUser(null, value, content));
                    }
                } else if (call.getCallMode() == 1) {
                    if (user.getId().equals(call.getUserIdTo())) {
                        Optional<User> toUser = userRepository.findById(call.getUserIdFrom());
                        String content = String.format("用户%s拨打已取消", user.getNick());
                        toUser.ifPresent(value -> sendToUser(null, value, content));
                    }
                }

                // 首次挂断如果是主播并且不是主播主动呼叫，则发送主播未接信息到wx
//                if (call.getCallMode() == 0) {
//                    if (user.getIdentity() == 1) {
//                        Long time = (now.getTime() - call.getCreatedTime().getTime()) / 1000;
//                        String message = String.format("1,%d,%d", call.getId(), time);
//                        Message<String> tipMsg = MessageBuilder.withPayload(message).build();
//                        boolean ret = wxWebHookBinding.wxWebHookOutputChannel().send(tipMsg);
//                        if (!ret) {
//                            // TODO: 2021/1/25 处理发送失败
//                        }
//                    } else {
//                        // 判断用户从拨打到挂断的时间超过5秒，则认为主播未接听
//                        long time = (now.getTime() - call.getCreatedTime().getTime()) / 1000;
//                        if (time > 5) {
//                            String message = String.format("2,%d,%d", call.getId(), time);
//                            Message<String> tipMsg = MessageBuilder.withPayload(message).build();
//                            boolean ret = wxWebHookBinding.wxWebHookOutputChannel().send(tipMsg);
//                            if (!ret) {
//                                // TODO: 2021/1/25 处理发送失败
//                            }
//                        }
//                    }
//                }

            } else {
                // 如果已经开始了 则计算总通话时长
                int deltaTime = (int) Math.ceil( (double) (now.getTime() - startTime.getTime()) / 1000L);
                call.setDuration(deltaTime);
            }

            callRepository.save(call);
        }

        // 判断是否在审核
        if (sysConfigService.isAppAudit() &&
                !StringUtils.isEmpty(version) &&
                sysConfigService.getAppAuditVersion().equals(version)) {
            // 过审版本 不显示时间
            resultMap.put("duration", 0);
        } else {
            resultMap.put("duration", call.getDuration());
        }

        resultMap.put("coin", 0);
        resultMap.put("card", 0);
        resultMap.put("userIdTo", "");  // 对方的数字id

        List<CallBill> callBillList = callBillRepository.findByCallId(call.getId());
        if (user.getId().equals(call.getUserIdFrom())) {
            // 计算消费
            Optional<BigDecimal> totalConsume =
                 callBillList.stream()
                         .map(CallBill::getOriginCoin).reduce(BigDecimal::add);
            totalConsume.ifPresent(bigDecimal -> resultMap.put("coin", BigDecimal.ZERO.subtract(bigDecimal)));

            Optional<Integer> cardType = callBillList.stream().map(CallBill::getSource)
                    .filter(s -> s > 0)
                    .findFirst();
            cardType.ifPresent( c -> resultMap.put("card", -1));

            Optional<User> userTo = userRepository.findById(call.getUserIdTo());
            resultMap.put("userIdTo", userTo.get().getDigitId());

        } else if (user.getId().equals(call.getUserIdTo())) {
            // 计算收益
            Optional<BigDecimal> totalProfit =
                    callBillList.stream()
                            .map(CallBill::getProfitCoin).reduce(BigDecimal::add);
            totalProfit.ifPresent(bigDecimal -> resultMap.put("coin", bigDecimal));

            Optional<Integer> cardType = callBillList.stream().map(CallBill::getSource)
                    .filter(s -> s > 0)
                    .findFirst();
            cardType.ifPresent( c -> resultMap.put("card", 1));

            Optional<User> userTo = userRepository.findById(call.getUserIdFrom());
            resultMap.put("userIdTo", userTo.get().getDigitId());
        }

        return resultMap;
    }

    @Transactional
    @Override
    public Map<String, Object> updateCall(User user, Long roomId, String version) throws ApiException {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("need_close", 0);
        resultMap.put("need_recharge", 0);
        resultMap.put("used_card", 0);

        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            throw new ApiException(-1, "通话已结束");
        }

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userTo = optionalUserTo.get();

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userFrom = optionalUserFrom.get();

        // step1: 更新通话信息
        Date now = new Date();
        if (Objects.isNull(call.getStartTime())) {
            // 设置开始通话时间
            call.setStartTime(now);
        } else {
            // 更新通话时长
            Date startTime = call.getStartTime();
            int deltaTime = (int) ((now.getTime() - startTime.getTime()) / 1000);
            call.setDuration(deltaTime);
        }
        callRepository.save(call);

        BigDecimal callPrice;
        // 判断是否在审核
        if (sysConfigService.isAppAudit() &&
                !StringUtils.isEmpty(version) &&
                sysConfigService.getAppAuditVersion().equals(version)) {
            // 过审版本 不计费
            callPrice = BigDecimal.ZERO;
        } else {
            // 获取通话费用
            callPrice = call.getType() == 0 ? userTo.getCallPrice() : userTo.getVideoPrice();
        }

        if (Objects.isNull(callPrice) || callPrice.compareTo(BigDecimal.ZERO) <= 0) {
            // 价格为0 或者 null 不计算收入
            return resultMap;
        }

        // step2： 计算收益
        Asset assetTo = assetRepository.findByUserId(call.getUserIdTo());

        // 当前通话总时长
        int duration = call.getDuration();
        // 当前的时长应该包含几个阶段的付费
        int stage = (int) ((duration / 60 )) + 1;

        List<CallBill> callBillList = callBillRepository.findByCallId(call.getId());
        int existStage = callBillList.size();

        List<CallBill> newCallBillList = new ArrayList<>();
        for (int i = existStage; i < stage; i++) {

            // 生成流水
            CallBill callBill = new CallBill();
            callBill.setCallId(call.getId());
            callBill.setUserIdFrom(call.getUserIdFrom());
            callBill.setUserIdTo(call.getUserIdTo());
            callBill.setSerialNumber(createBillSerialNumber());
            callBill.setType(call.getType());
            callBill.setStage(i);

            // 判断本次通话是否已经使用过优惠券等
            boolean usedCoupon = false;
            List<CallBill> callBillWithTypeList = callBillRepository.findByCallIdAndSourceNot(call.getId(), 0);
            if (callBillWithTypeList.size() <= 0) {

                // 判断是否有优惠券
                List<Coupon> couponList = couponService.getCallFreeCoupon(userFrom, call.getType());
                Optional<Coupon> optionalCoupon = couponList.stream()
                        .filter(couponService::isCouponValid)
                        .findFirst();

                if (optionalCoupon.isPresent()) {
                    Coupon availableCoupon = optionalCoupon.get();
                    // 使用优惠券
                    availableCoupon.setCount(availableCoupon.getCount() - 1);
                    couponRepository.save(availableCoupon);

                    // 设置流水，使用优惠券不计算收益
                    callBill.setSource(availableCoupon.getType());
                    callBill.setOriginCoin(BigDecimal.ZERO);
                    callBill.setCommissionCoin(BigDecimal.ZERO);
                    callBill.setCommissionRatio(BigDecimal.ZERO);
                    callBill.setProfitCoin(BigDecimal.ZERO);
                    callBill.setRemark("使用了优惠券");

                    usedCoupon = true;
                    resultMap.put("used_card", 1);
                }
            }

            // 如果没有使用优惠券
            if (!usedCoupon) {
                // 因为这里可能有多条数据，如果直接抛出异常，会导致可以结算的部分没结算,
                // 所以金币不足，也正常记录，但收益都是0
                Asset assetFrom = assetRepository.findByUserId(call.getUserIdFrom());
                if (assetFrom.getCoin().compareTo(callPrice) < 0) {
                    callBill.setOriginCoin(BigDecimal.ZERO);
                    callBill.setCommissionCoin(BigDecimal.ZERO);
                    callBill.setCommissionRatio(BigDecimal.ZERO);
                    callBill.setProfitCoin(BigDecimal.ZERO);
                    callBill.setRemark("用户金币不足");

                    // 提示前端结束通话
                    resultMap.put("need_close", 1);
                } else {

                    // 扣款
                    assetRepository.subCoin(call.getUserIdFrom(), callPrice);

                    // 计算收益抽成
                    BigDecimal profitRatio = call.getType() == 0 ? assetTo.getVoiceProfitRatio() : assetTo.getVideoProfitRatio();
                    BigDecimal commissionRatio = BigDecimal.ONE.subtract(profitRatio);
                    BigDecimal commissionCoin = commissionRatio.multiply(callPrice);
                    // 直接加金币 去掉抽成价格
                    BigDecimal coinProfit = callPrice.subtract(commissionCoin);
                    assetRepository.addCoin(assetTo.getUserId(), coinProfit);

                    callBill.setOriginCoin(callPrice);
                    callBill.setCommissionCoin(commissionCoin);
                    callBill.setCommissionRatio(commissionRatio);
                    callBill.setProfitCoin(coinProfit);
                }
            }

            newCallBillList.add(callBill);
        }

        if (newCallBillList.size() > 0) {
            callBillRepository.saveAll(newCallBillList);
        }

        // 返回相关数据给前端
        resultMap.put("duration", duration);

        // 只有发起通话的人才会收到具体信息
        if (user.getId().equals(call.getUserIdFrom())) {
            // 获取最新的余额是否足够一分钟通话，不足提醒需要充值
            Asset assetFromLatest = assetRepository.findByUserId(userFrom.getId());
            BigDecimal totalPrice = BigDecimal.valueOf(2).multiply(callPrice);
            if (assetFromLatest.getCoin().compareTo(totalPrice) < 0) {
                resultMap.put("need_recharge", 1);
            }
        }

        return resultMap;
    }

    @Transactional
    @Override
    public Long callGen(User userFrom, User userTo, int type) throws ApiException {
        if (userFrom.getIdentity() == 0 && userTo.getIdentity() == 0) {
            throw new ApiException(-1, "不支持该通话模式");
        }

        if (userFrom.getIdentity() == 1 && userTo.getIdentity() == 1) {
            throw new ApiException(-1, "不支持该通话模式");
        }

        if (activeService.isCallBusy(userFrom)) {
            throw new ApiException(-1, "您当前正忙，无法呼出");
        }

        if (activeService.isCallBusy(userTo)) {
            throw new ApiException(-1, "对方正在通话中，请稍后再试");
        }

        // 创建通话房间
        Call call = new Call();
        call.setType(type);

        if (userFrom.getIdentity() == 1 && userTo.getIdentity() == 0) {
            // 主播打给用户
            call.setUserIdFrom(userTo.getId());
            call.setUserIdTo(userFrom.getId());
            call.setCallMode(1);
        } else if (userFrom.getIdentity() == 0 && userTo.getIdentity() == 1) {
            // 用户打给用户
            call.setUserIdFrom(userFrom.getId());
            call.setUserIdTo(userTo.getId());
            call.setCallMode(0);
        }

        call.setRoomId(Long.valueOf(genRoomId()));
        call.setCommId(UUID.randomUUID().toString());

        call = callRepository.save(call);
        return call.getRoomId();
    }

    @SneakyThrows
    @Transactional
    @Override
    public int acceptGen(User user, Long roomId) throws ApiException {
        // 取消超时
        callCalcJobScheduler.stopCallTimeout(roomId);

        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            throw new ApiException(-1, "通话已结束");
        }

        if (Objects.nonNull(call.getStartTime())) {
            throw new ApiException(-1, "通话已经开始");
        }

        if (activeService.isCallBusy(user)) {
            throw new ApiException(-1, "您当前正忙，无法接听");
        }

        // 记录用户通话状态
        activeService.acceptCall(user, roomId);

        // 设定开始时间
        call.setStartTime(new Date());
        callRepository.save(call);

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userFrom = optionalUserFrom.get();

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userTo = optionalUserTo.get();

        // 先计算一次
        // 获取通话费用, userTo 永远是主播
        BigDecimal callPrice = call.getType() == 0 ? userTo.getCallPrice() : userTo.getVideoPrice();;

        if (Objects.nonNull(callPrice) && callPrice.compareTo(BigDecimal.ZERO) > 0) {
            // 计算收益
            Asset assetTo = assetRepository.findByUserId(call.getUserIdTo());

            // 生成流水
            CallBill callBill = new CallBill();
            callBill.setCallId(call.getId());
            callBill.setUserIdFrom(call.getUserIdFrom());
            callBill.setUserIdTo(call.getUserIdTo());
            callBill.setSerialNumber(createBillSerialNumber());
            callBill.setType(call.getType());
            callBill.setStage(0);

            // 判断是否有优惠券
            List<Coupon> couponList = couponService.getCallFreeCoupon(userFrom, call.getType());
            Optional<Coupon> optionalCoupon = couponList.stream()
                    .filter(couponService::isCouponValid)
                    .findFirst();

            if (optionalCoupon.isPresent()) {
                Coupon availableCoupon = optionalCoupon.get();
                // 使用优惠券
                availableCoupon.setCount(availableCoupon.getCount() - 1);
                couponRepository.save(availableCoupon);

                // 设置流水，使用优惠券不计算收益
                callBill.setSource(availableCoupon.getType());
                callBill.setOriginCoin(BigDecimal.ZERO);
                callBill.setCommissionCoin(BigDecimal.ZERO);
                callBill.setCommissionRatio(BigDecimal.ZERO);
                callBill.setProfitCoin(BigDecimal.ZERO);
                callBill.setRemark("使用了优惠券");
            } else {
                Asset assetFrom = assetRepository.findByUserId(call.getUserIdFrom());
                if (assetFrom.getCoin().compareTo(callPrice) < 0) {
                    throw new ApiException(-1, "对方聊币不足，无法接听");
                } else {
                    // 扣款
                    assetRepository.subCoin(call.getUserIdFrom(), callPrice);

                    // 计算收益抽成
                    BigDecimal profitRatio = call.getType() == 0 ? assetTo.getVoiceProfitRatio() : assetTo.getVideoProfitRatio();
                    BigDecimal commissionRatio = BigDecimal.ONE.subtract(profitRatio);
                    BigDecimal commissionCoin = commissionRatio.multiply(callPrice);
                    // 直接加金币 去掉抽成价格
                    BigDecimal coinProfit = callPrice.subtract(commissionCoin);
                    assetRepository.addCoin(assetTo.getUserId(), coinProfit);

                    callBill.setOriginCoin(callPrice);
                    callBill.setCommissionCoin(commissionCoin);
                    callBill.setCommissionRatio(commissionRatio);
                    callBill.setProfitCoin(coinProfit);
                }
            }

            callBillRepository.save(callBill);
        }

        if (call.getCallMode() == 0) {
            // 由用户发起通话

            // 判断当前用户是否在通话中

            // 发送自定义消息
            sendCallMsg(userTo, userFrom, 5, roomId, call.getType());

        } else if (call.getCallMode() == 1) {
            // 由主播发起通话

            // 判断当前用户是否在通话中

            // 发送自定义消息
            sendCallMsg(userFrom, userTo, 5, roomId, call.getType());
        }

        // 启动计费
        callCalcJobScheduler.startCallCalc(roomId);
        callCalcJobScheduler.startCallHeartbeat(roomId);

        return 1;
    }

    @SneakyThrows
    @Transactional
    @Override
    public int inviteGen(User user, Long roomId) throws ApiException {
        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            throw new ApiException(-1, "通话已结束");
        }

        if (Objects.nonNull(call.getStartTime())) {
            throw new ApiException(-1, "通话已经开始");
        }

        if (activeService.isCallBusy(user)) {
            throw new ApiException(-1, "您当前正忙，无法呼出");
        }

        // 记录用户通话状态
        activeService.inviteCall(user, roomId);

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userFrom = optionalUserFrom.get();

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userTo = optionalUserTo.get();

        if (call.getCallMode() == 0) {
            // 由用户发起通话

            // 判断当前用户是否在通话中

            // 发送自定义消息
            sendCallMsg(userFrom, userTo, 0, roomId, call.getType());

        } else if (call.getCallMode() == 1) {
            // 由主播发起通话

            // 判断当前用户是否在通话中

            // 发送自定义消息
            sendCallMsg(userTo, userFrom, 0, roomId, call.getType());
        }

        // 启动超时
        callCalcJobScheduler.startCallTimeout(roomId);

        return 1;
    }

    @SneakyThrows
    @Override
    public int cancelGen(User user, Long roomId) throws ApiException {
        // 取消计时
        callCalcJobScheduler.stopCallTimeout(roomId);

        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            throw new ApiException(-1, "通话已结束");
        }

        if (Objects.nonNull(call.getStartTime())) {
            throw new ApiException(-1, "通话已开始");
        }

        // 通话设置成结束
        call.setIsFinished(1);
        call.setFinishTime(new Date());
        callRepository.save(call);

        // 记录用户通话状态
        activeService.cancelCall(user, roomId);

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userFrom = optionalUserFrom.get();

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userTo = optionalUserTo.get();

        if (call.getCallMode() == 0) {
            // 由用户发起通话

            // 发送自定义消息
            sendCallMsg(userFrom, userTo, 1, roomId, call.getType());

        } else if (call.getCallMode() == 1) {
            // 由主播发起通话

            // 发送自定义消息
            sendCallMsg(userTo, userFrom, 1, roomId, call.getType());
        }

        return 1;
    }

    @SneakyThrows
    @Override
    public int rejectGen(User user, Long roomId) throws ApiException {
        // 取消计时
        callCalcJobScheduler.stopCallTimeout(roomId);

        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            throw new ApiException(-1, "通话已结束");
        }

        if (Objects.nonNull(call.getStartTime())) {
            throw new ApiException(-1, "通话已开始");
        }

        // 通话设置成结束
        call.setIsFinished(1);
        call.setFinishTime(new Date());
        callRepository.save(call);

        // 记录用户通话状态
        activeService.rejectCall(user, roomId);

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userFrom = optionalUserFrom.get();

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userTo = optionalUserTo.get();

        if (call.getCallMode() == 0) {
            // 由用户发起通话

            // 发送自定义消息
            sendCallMsg(userTo, userFrom, 2, roomId, call.getType());

        } else if (call.getCallMode() == 1) {
            // 由主播发起通话

            // 发送自定义消息
            sendCallMsg(userFrom, userTo, 2, roomId, call.getType());
        }

        return 1;
    }

    @Transactional
    @Override
    public int timeoutGen(Long roomId) throws ApiException {
        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            throw new ApiException(-1, "通话已结束");
        }

        if (Objects.nonNull(call.getStartTime())) {
            throw new ApiException(-1, "通话已开始");
        }

        // 通话设置成结束
        call.setIsFinished(1);
        call.setFinishTime(new Date());
        callRepository.save(call);

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userFrom = optionalUserFrom.get();

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userTo = optionalUserTo.get();

        // 记录用户通话状态
        activeService.timeoutCall(userFrom, roomId);
        activeService.timeoutCall(userTo, roomId);

        if (call.getCallMode() == 0) {
            // 由用户发起通话

            // 发送自定义消息
            sendCallMsg(userTo, userFrom, 3, roomId, call.getType());

        } else if (call.getCallMode() == 1) {
            // 由主播发起通话

            // 发送自定义消息
            sendCallMsg(userFrom, userTo, 3, roomId, call.getType());
        }

        return 1;
    }

    @SneakyThrows
    @Override
    public int endGen(User user, Long roomId) throws ApiException {
        // 停止计时
        callCalcJobScheduler.stopCallCalc(roomId);
        callCalcJobScheduler.stopCallHeartbeat(roomId);

        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            throw new ApiException(-1, "通话已结束");
        }

        if (Objects.isNull(call.getStartTime())) {
            throw new ApiException(-1, "通话还未开始");
        }

        // 通话设置成结束
        Date now = new Date();
        call.setIsFinished(1);
        call.setFinishTime(new Date());

        // 计算时长
        int deltaTime = (int) Math.ceil( (double) (now.getTime() - call.getStartTime().getTime()) / 1000L);
        call.setDuration(deltaTime);

        callRepository.save(call);

        // 记录用户通话状态
        activeService.endCall(user, roomId);

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userFrom = optionalUserFrom.get();

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userTo = optionalUserTo.get();

        if (call.getCallMode() == 0) {
            // 由用户发起通话

            // 发送自定义消息
            sendCallMsg(userFrom, userTo, 4, roomId, call.getType());

        } else if (call.getCallMode() == 1) {
            // 由主播发起通话

            // 发送自定义消息
            sendCallMsg(userTo, userFrom, 4, roomId, call.getType());
        }

        return 1;
    }

    @Transactional
    @SneakyThrows
    @Override
    public int endGenByServer(Long roomId) throws ApiException {
        // 停止计时
        callCalcJobScheduler.stopCallCalc(roomId);
        // 停止心跳检测
        callCalcJobScheduler.stopCallHeartbeat(roomId);

        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            return -1;
        }

        if (Objects.isNull(call.getStartTime())) {
            return -2;
        }

        // 通话设置成结束
        Date now = new Date();
        call.setIsFinished(1);
        call.setFinishTime(new Date());

        // 计算时长
        int deltaTime = (int) Math.ceil( (double) (now.getTime() - call.getStartTime().getTime()) / 1000L);
        call.setDuration(deltaTime);

        callRepository.save(call);

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userFrom = optionalUserFrom.get();

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userTo = optionalUserTo.get();

        // 记录用户通话状态
        activeService.endCall(userFrom, roomId);
        activeService.endCall(userTo, roomId);

        if (call.getCallMode() == 0) {
            // 由用户发起通话

            // 发送自定义消息
            sendCallMsg(userFrom, userTo, 4, roomId, call.getType());

        } else if (call.getCallMode() == 1) {
            // 由主播发起通话

            // 发送自定义消息
            sendCallMsg(userTo, userFrom, 4, roomId, call.getType());
        }

        return 1;
    }

    @Transactional
    @Override
    public int hangupGen(User user, Long roomId) throws ApiException {
        Call call = callRepository.findByRoomId(roomId);

        // 判断是否已结束
        if (call.getIsFinished() == 1) {
            throw new ApiException(-1, "通话已结束");
        }

        // 判断是否已经开始
        if (Objects.isNull(call.getStartTime())) {
            // 未开始，判断是邀请人还是接听人
            if (call.getCallMode() == 0) {
                if (user.getId().equals(call.getUserIdFrom())) {
                    // 用户主动挂断电话
                    return cancelGen(user, roomId);
                } else if (user.getId().equals(call.getUserIdTo())) {
                    // 主播拒绝通话
                    return rejectGen(user, roomId);
                }
            } else if (call.getCallMode() == 1) {
                if (user.getId().equals(call.getUserIdTo())) {
                    // 主播主动挂断电话
                    return cancelGen(user, roomId);
                } else if (user.getId().equals(call.getUserIdFrom())) {
                    // 用户拒绝通话
                    return rejectGen(user, roomId);
                }
            }
        } else {
            // 已开始
            return endGen(user, roomId);
        }

        return 0;
    }

    @Override
    public Map<String, Object> resultGen(User user, Long roomId) throws ApiException {

        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 0) {
            throw new ApiException(-1, "通话还未结束");
        }

        if (Objects.isNull(call.getStartTime())) {
            throw new ApiException(-1, "通话还未开始");
        }

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userFrom = optionalUserFrom.get();

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userTo = optionalUserTo.get();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("duration", call.getDuration());

        resultMap.put("coin", 0);
        resultMap.put("card", 0);
        resultMap.put("userIdTo", "");  // 对方的数字id

        List<CallBill> callBillList = callBillRepository.findByCallId(call.getId());
        if (user.getId().equals(call.getUserIdFrom())) {
            // 计算消费
            Optional<BigDecimal> totalConsume =
                    callBillList.stream()
                            .map(CallBill::getOriginCoin).reduce(BigDecimal::add);
            totalConsume.ifPresent(bigDecimal -> resultMap.put("coin", BigDecimal.ZERO.subtract(bigDecimal)));

            Optional<Integer> cardType = callBillList.stream().map(CallBill::getSource)
                    .filter(s -> s > 0)
                    .findFirst();
            cardType.ifPresent( c -> resultMap.put("card", -1));

            resultMap.put("userIdTo", userTo.getDigitId());

        } else if (user.getId().equals(call.getUserIdTo())) {
            // 计算收益
            Optional<BigDecimal> totalProfit =
                    callBillList.stream()
                            .map(CallBill::getProfitCoin).reduce(BigDecimal::add);
            totalProfit.ifPresent(bigDecimal -> resultMap.put("coin", bigDecimal));

            Optional<Integer> cardType = callBillList.stream().map(CallBill::getSource)
                    .filter(s -> s > 0)
                    .findFirst();
            cardType.ifPresent( c -> resultMap.put("card", 1));

            resultMap.put("userIdTo", userFrom.getDigitId());
        }

        return resultMap;
    }

    @Override
    public Map<String, Object> updateGen(User user, Long roomId) throws ApiException {
        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            throw new ApiException(-1, "通话已经结束");
        }

        if (Objects.isNull(call.getStartTime())) {
            throw new ApiException(-1, "通话还未开始");
        }

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userFrom = optionalUserFrom.get();

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }
        User userTo = optionalUserTo.get();

        // 记录用户通话状态
        activeService.updateCall(user, roomId);

        Map<String, Object> resultMap = new HashMap<>();
        int deltaTime = (int) Math.ceil( (double) ( new Date().getTime() - call.getStartTime().getTime()) / 1000L);
        resultMap.put("duration", deltaTime);

        if (user.getId().equals(userFrom.getId())) {
            // 通知用户是否需要充值

            // 获取最新的余额是否足够一分钟通话，不足提醒需要充值
            Asset assetFrom = assetRepository.findByUserId(userFrom.getId());
            BigDecimal totalPrice = BigDecimal.valueOf(2).multiply(userTo.getCallPrice());
            if (assetFrom.getCoin().compareTo(totalPrice) < 0) {
                resultMap.put("need_recharge", 1);
            } else {
                resultMap.put("need_recharge", 0);
            }
        }

        return resultMap;
    }

    @Transactional
    @Override
    public int calcGen(Long roomId) throws ApiException {
        Call call = callRepository.findByRoomId(roomId);
        if (call.getIsFinished() == 1) {
            return -1;
        }

        if (Objects.isNull(call.getStartTime())) {
            return -1;
        }

        Optional<User> optionalUserTo = userRepository.findById(call.getUserIdTo());
        if (!optionalUserTo.isPresent()) {
            return -1;
        }
        User userTo = optionalUserTo.get();

        Optional<User> optionalUserFrom = userRepository.findById(call.getUserIdFrom());
        if (!optionalUserFrom.isPresent()) {
            return -1;
        }
        User userFrom = optionalUserFrom.get();

        // step1: 更新通话信息
        Date now = new Date();
        // 更新通话时长
        Date startTime = call.getStartTime();
        int deltaTime = (int) Math.ceil( (double) (now.getTime() - startTime.getTime()) / 1000L);
        call.setDuration(deltaTime);

        // save to db
        callRepository.save(call);

        // 是否需要结束通话
        boolean needFinish = false;

        // 获取通话费用, userTo 永远是主播
        BigDecimal callPrice = call.getType() == 0 ? userTo.getCallPrice() : userTo.getVideoPrice();;

        if (Objects.isNull(callPrice) || callPrice.compareTo(BigDecimal.ZERO) <= 0) {
            // 价格为0 或者 null 不计算收入
            return 0;
        }

        // step2： 计算收益
        Asset assetTo = assetRepository.findByUserId(call.getUserIdTo());

        // 当前通话总时长
        int duration = call.getDuration();
        // 当前的时长应该包含几个阶段的付费
        int stage = (int) ((duration / 60 )) + 1;

        List<CallBill> callBillList = callBillRepository.findByCallId(call.getId());
        int existStage = callBillList.size();

        List<CallBill> newCallBillList = new ArrayList<>();
        for (int i = existStage; i < stage; i++) {

            // 生成流水
            CallBill callBill = new CallBill();
            callBill.setCallId(call.getId());
            callBill.setUserIdFrom(call.getUserIdFrom());
            callBill.setUserIdTo(call.getUserIdTo());
            callBill.setSerialNumber(createBillSerialNumber());
            callBill.setType(call.getType());
            callBill.setStage(i);

            // 判断本次通话是否已经使用过优惠券等
            boolean usedCoupon = false;
            List<CallBill> callBillWithTypeList = callBillRepository.findByCallIdAndSourceNot(call.getId(), 0);
            if (callBillWithTypeList.size() <= 0) {

                // 判断是否有优惠券
                List<Coupon> couponList = couponService.getCallFreeCoupon(userFrom, call.getType());
                Optional<Coupon> optionalCoupon = couponList.stream()
                        .filter(couponService::isCouponValid)
                        .findFirst();

                if (optionalCoupon.isPresent()) {
                    Coupon availableCoupon = optionalCoupon.get();
                    // 使用优惠券
                    availableCoupon.setCount(availableCoupon.getCount() - 1);
                    couponRepository.save(availableCoupon);

                    // 设置流水，使用优惠券不计算收益
                    callBill.setSource(availableCoupon.getType());
                    callBill.setOriginCoin(BigDecimal.ZERO);
                    callBill.setCommissionCoin(BigDecimal.ZERO);
                    callBill.setCommissionRatio(BigDecimal.ZERO);
                    callBill.setProfitCoin(BigDecimal.ZERO);
                    callBill.setRemark("使用了优惠券");

                    usedCoupon = true;
                }
            }

            // 如果没有使用优惠券
            if (!usedCoupon) {
                // 因为这里可能有多条数据，如果直接抛出异常，会导致可以结算的部分没结算,
                // 所以金币不足，也正常记录，但收益都是0
                Asset assetFrom = assetRepository.findByUserId(call.getUserIdFrom());
                if (assetFrom.getCoin().compareTo(callPrice) < 0) {
                    callBill.setOriginCoin(BigDecimal.ZERO);
                    callBill.setCommissionCoin(BigDecimal.ZERO);
                    callBill.setCommissionRatio(BigDecimal.ZERO);
                    callBill.setProfitCoin(BigDecimal.ZERO);
                    callBill.setRemark("用户金币不足");

                    // 结束通话通知
                    needFinish = true;
                } else {

                    // 扣款
                    assetRepository.subCoin(call.getUserIdFrom(), callPrice);

                    // 计算收益抽成
                    BigDecimal profitRatio = call.getType() == 0 ? assetTo.getVoiceProfitRatio() : assetTo.getVideoProfitRatio();
                    BigDecimal commissionRatio = BigDecimal.ONE.subtract(profitRatio);
                    BigDecimal commissionCoin = commissionRatio.multiply(callPrice);
                    // 直接加金币 去掉抽成价格
                    BigDecimal coinProfit = callPrice.subtract(commissionCoin);
                    assetRepository.addCoin(assetTo.getUserId(), coinProfit);

                    callBill.setOriginCoin(callPrice);
                    callBill.setCommissionCoin(commissionCoin);
                    callBill.setCommissionRatio(commissionRatio);
                    callBill.setProfitCoin(coinProfit);
                }
            }

            newCallBillList.add(callBill);
        }

        if (newCallBillList.size() > 0) {
            callBillRepository.saveAll(newCallBillList);
        }

        if (needFinish) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     *【功能说明】用于签发 TRTC 和 IM 服务中必须要使用的 UserSig 鉴权票据
     */
    @Override
    public String genUserSign(User user) {
        return genUserSig(user.getDigitId(), imConfProp.getExpire(), null);
    }

    @Override
    public int sendToUser(User fromUser, User toUser, String content) throws ApiException {
        if (Objects.isNull(toUser)) {
            throw new ApiException(-1, "目标用户不存在!");
        }
        String fromAccount = Objects.isNull(fromUser) ? null : fromUser.getDigitId();
        ImSendMsgDto imSendMsgDto = ImMsgFactory.buildTextMsg(fromAccount, toUser.getDigitId(), content, true);

        // 获取管理员的 usersig
        String admin = imConfProp.getAdmin();
        String adminSig = genAdminSig(admin);

        ImSendMsgCbResp imSendMsgCbResp = imFeign
                .sendMsg(imConfProp.getSdkAppId(), admin, adminSig, new Random().nextInt(), "json", imSendMsgDto);

        if (!imSendMsgCbResp.getActionStatus().equals("OK")) {
            throw new ApiException(-1, imSendMsgCbResp.getErrorInfo());
        }

        // TODO: 2020/12/29 记录消息

        return 1;
    }

    @Override
    public int sendGiftMsg(User userFrom, User userTo, Gift gift, int count) throws ApiException {
//        ImSendMsgDto msg2From = ImMsgFactory
//                .buildGiftMsg(null, userTo.getDigitId(), "你收到了新的礼物!", 202, gift.getId(), count, true);
//        ImSendMsgDto msg2To = ImMsgFactory
//                .buildGiftMsg(null, userFrom.getDigitId(), "赠送礼物成功!", 201, gift.getId(), count, true);

        String headThumbnailUrl = null;
        if (!StringUtils.isEmpty(userFrom.getHead())) {
            headThumbnailUrl = String.format("%s/%s/thumbnail_%s",
                    ossConfProp.getMinioVisitUrl(),
                    ossConfProp.getUserProfileBucket(),
                    userFrom.getHead());
        }
        ImSendMsgDto msgCustom = ImMsgFactory.buildGiftMsg(userFrom.getDigitId(), userTo.getDigitId(),
                "您收到一个礼物，快去看看吧", 203, gift.getId(), count, gift.getName(),
                userTo.getNick(), headThumbnailUrl,
                true);

        // 获取管理员的 usersig
        String admin = imConfProp.getAdmin();
        String adminSig = genAdminSig(admin);

        // 发送给发送方,接收方也能监听到消息
        ImSendMsgCbResp imSendMsgCbResp = imFeign
                .sendMsg(imConfProp.getSdkAppId(), admin, adminSig, new Random().nextInt(), "json", msgCustom);

        if (!imSendMsgCbResp.getActionStatus().equals("OK")) {
            throw new ApiException(-1, imSendMsgCbResp.getErrorInfo());
        }

//        // 发送给发送方
//        ImSendMsgCbResp imSendMsgCbResp = imFeign
//                .sendMsg(imConfProp.getSdkAppId(), admin, adminSig, new Random().nextInt(), "json", msg2From);
//
//        if (!imSendMsgCbResp.getActionStatus().equals("OK")) {
//            throw new ApiException(-1, imSendMsgCbResp.getErrorInfo());
//        }
//
//        // 发送给接收方
//        imSendMsgCbResp = imFeign
//                .sendMsg(imConfProp.getSdkAppId(), admin, adminSig, new Random().nextInt(), "json", msg2To);
//
//        if (!imSendMsgCbResp.getActionStatus().equals("OK")) {
//            throw new ApiException(-1, imSendMsgCbResp.getErrorInfo());
//        }

        // TODO: 2020/12/29 记录消息

        return 1;

    }

    @Override
    public int sendCallMsg(User userFrom, User userTo, int mode, long roomId, int roomType) throws ApiException {
        ImSendMsgDto msgCustom = null;
        if (mode == 0) {
            String formatString = String.format("invite-call,%d,%d", roomId, roomType);
            msgCustom = ImMsgFactory.buildCallMsg(userFrom.getDigitId(), userTo.getDigitId(),
                    "发起通话", formatString, true);
        } else if (mode == 1) {
            String formatString = String.format("cancel-call,%d,%d", roomId, roomType);
            msgCustom = ImMsgFactory.buildCallMsg(userFrom.getDigitId(), userTo.getDigitId(),
                    "取消通话", formatString, true);
        } else if (mode == 2) {
            String formatString = String.format("reject-call,%d,%d", roomId, roomType);
            msgCustom = ImMsgFactory.buildCallMsg(userFrom.getDigitId(), userTo.getDigitId(),
                    "拒绝通话", formatString, true);
        } else if (mode == 3) {
            String formatString = String.format("timeout-call,%d,%d", roomId, roomType);
            msgCustom = ImMsgFactory.buildCallMsg(userFrom.getDigitId(), userTo.getDigitId(),
                    "通话超时", formatString, true);
        } else if (mode == 4) {
            String formatString = String.format("end-call,%d,%d", roomId, roomType);
            msgCustom = ImMsgFactory.buildCallMsg(userFrom.getDigitId(), userTo.getDigitId(),
                    "通话结束", formatString, true);
        } else if (mode == 5) {
            String formatString = String.format("accept-call,%d,%d", roomId, roomType);
            msgCustom = ImMsgFactory.buildCallMsg(userFrom.getDigitId(), userTo.getDigitId(),
                    "已接听", formatString, true);
        }

        if (msgCustom != null) {

            // 获取管理员的 usersig
            String admin = imConfProp.getAdmin();
            String adminSig = genAdminSig(admin);

            // 发送给发送方,接收方也能监听到消息
            ImSendMsgCbResp imSendMsgCbResp = imFeign
                    .sendMsg(imConfProp.getSdkAppId(), admin, adminSig, new Random().nextInt(), "json", msgCustom);

            if (!imSendMsgCbResp.getActionStatus().equals("OK")) {
                throw new ApiException(-1, imSendMsgCbResp.getErrorInfo());
            }

            return 1;
        }

        return 0;
    }

    @Override
    public int sendAfterSendMsg(User userFrom, User userTo, int tag, int coin, int card) throws ApiException {
        ImSendMsgDto msgCustom = ImMsgFactory.buildChargingMsg(userFrom.getDigitId(), userTo.getDigitId(),
                "一条扣费信息", tag, coin, card, true);

        // 获取管理员的 usersig
        String admin = imConfProp.getAdmin();
        String adminSig = genAdminSig(admin);

        ImSendMsgCbResp imSendMsgCbResp = imFeign
                .sendMsg(imConfProp.getSdkAppId(), admin, adminSig, new Random().nextInt(), "json", msgCustom);

        if (!imSendMsgCbResp.getActionStatus().equals("OK")) {
            throw new ApiException(-1, imSendMsgCbResp.getErrorInfo());
        }

        return 1;
    }

    @Override
    public ImQueryStateResp queryState(List<String> accountList) throws ApiException {
        ImQueryStateDto imQueryStateDto = new ImQueryStateDto();
        imQueryStateDto.setTo_Account(accountList);

        // 获取管理员的 usersig
        String admin = imConfProp.getAdmin();
        String adminSig = genAdminSig(admin);

        ImQueryStateResp queryStateResp = imFeign.queryState(imConfProp.getSdkAppId(), admin, adminSig,
                new Random().nextInt(), "json", imQueryStateDto);

        if (!queryStateResp.getActionStatus().equals("OK")) {
            throw new ApiException(-1, queryStateResp.getErrorInfo());
        }

        return queryStateResp;
    }

    @SneakyThrows
    @Override
    public int trtcDismissRoom(Long roomId) throws ApiException {
        DismissRoomRequest dismissRoomRequest = new DismissRoomRequest();
        dismissRoomRequest.setRoomId(roomId);
        dismissRoomRequest.setSdkAppId(imConfProp.getSdkAppId());

        Credential credential = new Credential(imConfProp.getSecretId(), imConfProp.getSecretKey());


        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("trtc.tencentcloudapi.com");

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        TrtcClient client = new TrtcClient(credential, "ap-guangzhou", clientProfile);
        client.DismissRoom(dismissRoomRequest);

        return 1;
    }

    /**
     * 生成管理员账户的 user sig
     */
    private String genAdminSig(String admin) {
        return genUserSig(admin, imConfProp.getExpire(), null);
    }

    /**
     * 【功能说明】
     * 用于签发 TRTC 进房参数中可选的 PrivateMapKey 权限票据。
     * PrivateMapKey 需要跟 UserSig 一起使用，但 PrivateMapKey 比 UserSig 有更强的权限控制能力：
     * - UserSig 只能控制某个 UserID 有无使用 TRTC 服务的权限，只要 UserSig 正确，其对应的 UserID 可以进出任意房间。
     * - PrivateMapKey 则是将 UserID 的权限控制的更加严格，包括能不能进入某个房间，能不能在该房间里上行音视频等等。
     * 如果要开启 PrivateMapKey 严格权限位校验，需要在【实时音视频控制台】/【应用管理】/【应用信息】中打开“启动权限密钥”开关。
     * <p>
     * 【参数说明】
     *
     * @param userid       - 用户id，限制长度为32字节，只允许包含大小写英文字母（a-zA-Z）、数字（0-9）及下划线和连词符。
     * @param expire       - PrivateMapKey 票据的过期时间，单位是秒，比如 86400 生成的 PrivateMapKey 票据在一天后就无法再使用了。
     * @param roomid       - 房间号，用于指定该 userid 可以进入的房间号
     * @param privilegeMap - 权限位，使用了一个字节中的 8 个比特位，分别代表八个具体的功能权限开关：
     *                     - 第 1 位：0000 0001 = 1，创建房间的权限
     *                     - 第 2 位：0000 0010 = 2，加入房间的权限
     *                     - 第 3 位：0000 0100 = 4，发送语音的权限
     *                     - 第 4 位：0000 1000 = 8，接收语音的权限
     *                     - 第 5 位：0001 0000 = 16，发送视频的权限
     *                     - 第 6 位：0010 0000 = 32，接收视频的权限
     *                     - 第 7 位：0100 0000 = 64，发送辅路（也就是屏幕分享）视频的权限
     *                     - 第 8 位：1000 0000 = 200，接收辅路（也就是屏幕分享）视频的权限
     *                     - privilegeMap == 1111 1111 == 255 代表该 userid 在该 roomid 房间内的所有功能权限。
     *                     - privilegeMap == 0010 1010 == 42  代表该 userid 拥有加入房间和接收音视频数据的权限，但不具备其他权限。
     * @return usersig - 生成带userbuf的签名
     */
    public String genPrivateMapKey(String userid, long expire, long roomid, long privilegeMap) {
        byte[] userbuf = genUserBuf(userid, roomid, expire, privilegeMap, 0, "");  //生成userbuf
        return genUserSig(userid, expire, userbuf);
    }

    /**
     * 【功能说明】
     * 用于签发 TRTC 进房参数中可选的 PrivateMapKey 权限票据。
     * PrivateMapKey 需要跟 UserSig 一起使用，但 PrivateMapKey 比 UserSig 有更强的权限控制能力：
     * - UserSig 只能控制某个 UserID 有无使用 TRTC 服务的权限，只要 UserSig 正确，其对应的 UserID 可以进出任意房间。
     * - PrivateMapKey 则是将 UserID 的权限控制的更加严格，包括能不能进入某个房间，能不能在该房间里上行音视频等等。
     * 如果要开启 PrivateMapKey 严格权限位校验，需要在【实时音视频控制台】/【应用管理】/【应用信息】中打开“启动权限密钥”开关。
     * <p>
     * 【参数说明】
     *
     * @param userid       - 用户id，限制长度为32字节，只允许包含大小写英文字母（a-zA-Z）、数字（0-9）及下划线和连词符。
     * @param expire       - PrivateMapKey 票据的过期时间，单位是秒，比如 86400 生成的 PrivateMapKey 票据在一天后就无法再使用了。
     * @param roomstr      - 字符串房间号，用于指定该 userid 可以进入的房间号
     * @param privilegeMap - 权限位，使用了一个字节中的 8 个比特位，分别代表八个具体的功能权限开关：
     *                     - 第 1 位：0000 0001 = 1，创建房间的权限
     *                     - 第 2 位：0000 0010 = 2，加入房间的权限
     *                     - 第 3 位：0000 0100 = 4，发送语音的权限
     *                     - 第 4 位：0000 1000 = 8，接收语音的权限
     *                     - 第 5 位：0001 0000 = 16，发送视频的权限
     *                     - 第 6 位：0010 0000 = 32，接收视频的权限
     *                     - 第 7 位：0100 0000 = 64，发送辅路（也就是屏幕分享）视频的权限
     *                     - 第 8 位：1000 0000 = 200，接收辅路（也就是屏幕分享）视频的权限
     *                     - privilegeMap == 1111 1111 == 255 代表该 userid 在该 roomid 房间内的所有功能权限。
     *                     - privilegeMap == 0010 1010 == 42  代表该 userid 拥有加入房间和接收音视频数据的权限，但不具备其他权限。
     * @return usersig - 生成带userbuf的签名
     */
    public String genPrivateMapKeyWithStringRoomID(String userid, long expire, String roomstr, long privilegeMap) {
        byte[] userbuf = genUserBuf(userid, 0, expire, privilegeMap, 0, roomstr);  //生成userbuf
        return genUserSig(userid, expire, userbuf);
    }

    private String hmacsha256(String identifier, long currTime, long expire, String base64Userbuf) {
        String contentToBeSigned = "TLS.identifier:" + identifier + "\n"
                + "TLS.sdkappid:" + imConfProp.getSdkAppId() + "\n"
                + "TLS.time:" + currTime + "\n"
                + "TLS.expire:" + expire + "\n";
        if (null != base64Userbuf) {
            contentToBeSigned += "TLS.userbuf:" + base64Userbuf + "\n";
        }
        try {
            byte[] byteKey = imConfProp.getKey().getBytes(StandardCharsets.UTF_8);
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
            hmac.init(keySpec);
            byte[] byteSig = hmac.doFinal(contentToBeSigned.getBytes(StandardCharsets.UTF_8));
            return (Base64.getEncoder().encodeToString(byteSig)).replaceAll("\\s*", "");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }

    /**
     *【功能说明】用于签发 TRTC 和 IM 服务中必须要使用的 UserSig 鉴权票据
     * <p>
     * 【参数说明】
     *
     * @param userid - 用户id，限制长度为32字节，只允许包含大小写英文字母（a-zA-Z）、数字（0-9）及下划线和连词符。
     * @param expire - UserSig 票据的过期时间，单位是秒，比如 86400 代表生成的 UserSig 票据在一天后就无法再使用了。
     * @return usersig -生成的签名
     */
    @SneakyThrows
    private String genUserSig(String userid, long expire, byte[] userbuf) {

        long currTime = System.currentTimeMillis() / 1000;

        JSONObject sigDoc = new JSONObject();
        sigDoc.put("TLS.ver", "2.0");
        sigDoc.put("TLS.identifier", userid);
        sigDoc.put("TLS.sdkappid", imConfProp.getSdkAppId());
        sigDoc.put("TLS.expire", expire);
        sigDoc.put("TLS.time", currTime);

        String base64UserBuf = null;
        if (null != userbuf) {
            base64UserBuf = Base64.getEncoder().encodeToString(userbuf).replaceAll("\\s*", "");
            sigDoc.put("TLS.userbuf", base64UserBuf);
        }
        String sig = hmacsha256(userid, currTime, expire, base64UserBuf);
        if (sig.length() == 0) {
            return "";
        }
        sigDoc.put("TLS.sig", sig);
        Deflater compressor = new Deflater();
        compressor.setInput(sigDoc.toString().getBytes(StandardCharsets.UTF_8));
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedBytesLength = compressor.deflate(compressedBytes);
        compressor.end();
//        return (new String(Base64URL.base64EncodeUrl(Arrays.copyOfRange(compressedBytes,
//                0, compressedBytesLength)))).replaceAll("\\s*", "");
        return (new String(Base64.getEncoder().encode(Arrays.copyOfRange(compressedBytes,
                0, compressedBytesLength)))).replaceAll("\\s*", "");
    }

    public byte[] genUserBuf(String account, long dwAuthID, long dwExpTime,
                             long dwPrivilegeMap, long dwAccountType, String RoomStr) {
        //视频校验位需要用到的字段,按照网络字节序放入buf中
        /*
         cVer    unsigned char/1 版本号，填0
         wAccountLen unsigned short /2   第三方自己的帐号长度
         account wAccountLen 第三方自己的帐号字符
         dwSdkAppid  unsigned int/4  sdkappid
         dwAuthID    unsigned int/4  群组号码
         dwExpTime   unsigned int/4  过期时间 ，直接使用填入的值
         dwPrivilegeMap  unsigned int/4  权限位，主播0xff，观众0xab
         dwAccountType   unsigned int/4  第三方帐号类型
         */
        int accountLength = account.length();
        int roomStrLength = RoomStr.length();
        int offset = 0;
        int bufLength = 1 + 2 + accountLength + 20 ;
        if (roomStrLength > 0) {
            bufLength = bufLength + 2 + roomStrLength;
        }
        byte[] userbuf = new byte[bufLength];

        //cVer
        if (roomStrLength > 0) {
            userbuf[offset++] = 1;
        } else {
            userbuf[offset++] = 0;
        }

        //wAccountLen
        userbuf[offset++] = (byte) ((accountLength & 0xFF00) >> 8);
        userbuf[offset++] = (byte) (accountLength & 0x00FF);

        //account
        for (; offset < 3 + accountLength; ++offset) {
            userbuf[offset] = (byte) account.charAt(offset - 3);
        }

        //dwSdkAppid
        userbuf[offset++] = (byte) ((imConfProp.getSdkAppId() & 0xFF000000) >> 24);
        userbuf[offset++] = (byte) ((imConfProp.getSdkAppId() & 0x00FF0000) >> 16);
        userbuf[offset++] = (byte) ((imConfProp.getSdkAppId() & 0x0000FF00) >> 8);
        userbuf[offset++] = (byte) (imConfProp.getSdkAppId() & 0x000000FF);

        //dwAuthId,房间号
        userbuf[offset++] = (byte) ((dwAuthID & 0xFF000000) >> 24);
        userbuf[offset++] = (byte) ((dwAuthID & 0x00FF0000) >> 16);
        userbuf[offset++] = (byte) ((dwAuthID & 0x0000FF00) >> 8);
        userbuf[offset++] = (byte) (dwAuthID & 0x000000FF);

        //expire，过期时间,当前时间 + 有效期（单位：秒）
        long currTime = System.currentTimeMillis() / 1000;
        long expire = currTime + dwExpTime;
        userbuf[offset++] = (byte) ((expire & 0xFF000000) >> 24);
        userbuf[offset++] = (byte) ((expire & 0x00FF0000) >> 16);
        userbuf[offset++] = (byte) ((expire & 0x0000FF00) >> 8);
        userbuf[offset++] = (byte) (expire & 0x000000FF);

        //dwPrivilegeMap，权限位
        userbuf[offset++] = (byte) ((dwPrivilegeMap & 0xFF000000) >> 24);
        userbuf[offset++] = (byte) ((dwPrivilegeMap & 0x00FF0000) >> 16);
        userbuf[offset++] = (byte) ((dwPrivilegeMap & 0x0000FF00) >> 8);
        userbuf[offset++] = (byte) (dwPrivilegeMap & 0x000000FF);

        //dwAccountType，账户类型
        userbuf[offset++] = (byte) ((dwAccountType & 0xFF000000) >> 24);
        userbuf[offset++] = (byte) ((dwAccountType & 0x00FF0000) >> 16);
        userbuf[offset++] = (byte) ((dwAccountType & 0x0000FF00) >> 8);
        userbuf[offset++] = (byte) (dwAccountType & 0x000000FF);


        if (roomStrLength > 0) {
            //roomStrLen
            userbuf[offset++] = (byte) ((roomStrLength & 0xFF00) >> 8);
            userbuf[offset++] = (byte) (roomStrLength & 0x00FF);

            //roomStr
            for (; offset < bufLength; ++offset) {
                userbuf[offset] = (byte) RoomStr.charAt(offset - (bufLength - roomStrLength));
            }
        }
        return userbuf;
    }

    // 生成唯一房间id
    private Integer genRoomId() {
        Random random = new Random();

        // 为了以后能扩展， 保留 1Y以上的房间号
        Integer randomInt;
        do {
            randomInt = Math.abs(random.nextInt());
        } while (randomInt >= 100000000);

        return randomInt;
    }
}
