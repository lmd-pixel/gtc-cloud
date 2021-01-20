package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Coupon;
import com.fmisser.gtc.social.domain.User;

import java.util.List;

public interface CouponService {

    List<Coupon> getCouponList(User user) throws ApiException;

    List<Coupon> getMsgFreeCoupon(User user) throws ApiException;

    List<Coupon> getCallFreeCoupon(User user, int type) throws ApiException;

    boolean isCouponValid(Coupon coupon) throws ApiException;
}
