package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Coupon;
import com.fmisser.gtc.social.domain.User;

import java.util.List;

public interface CouponService {

    // 获取优惠券列表
    List<Coupon> getCouponList(User user) throws ApiException;

    // 获取消息免费券
    List<Coupon> getMsgFreeCoupon(User user) throws ApiException;

    // 获取通话免费券
    List<Coupon> getCallFreeCoupon(User user, int type) throws ApiException;

    // 判断券是否有效
    boolean isCouponValid(Coupon coupon) throws ApiException;

    // 添加普通的消息免费券
    int addCommMsgFreeCoupon(Long userId, int count) throws ApiException;

    // 添加普通的语音免费券
    int addCommVoiceCoupon(Long userId, int count) throws ApiException;

    // 添加普通的视频免费券
    int addCommVideoCoupon(Long userId, int count) throws ApiException;
}
