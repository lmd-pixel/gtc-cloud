package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Coupon;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.CouponRepository;
import com.fmisser.gtc.social.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class CouponServiceImpl implements CouponService {

    // TODO: 2021/1/20 改成配置
    private final String CommonFreeMsgCoupon = "comm_msg_free_coupon";
    private final String CommonFreeVoiceCoupon = "comm_voice_free_coupon";
    private final String CommonFreeVideoCoupon = "comm_video_free_coupon";

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public List<Coupon> getCouponList(User user) throws ApiException {
        return couponRepository.findByUserId(user.getId());
    }

    @Override
    public List<Coupon> getCallFreeCoupon(User user, int type) throws ApiException {
        if (type == 0) {
            // 语音
            return couponRepository.findByUserIdAndType(user.getId(), 20);
        } else  {
            // 视频
            return couponRepository.findByUserIdAndType(user.getId(), 30);
        }
    }

    @Override
    public List<Coupon> getMsgFreeCoupon(User user) throws ApiException {
        return couponRepository.findByUserIdAndType(user.getId(), 10);
    }

    @Override
    public boolean isCouponValid(Coupon coupon) throws ApiException {

        if (Objects.isNull(coupon)) {
            return false;
        }

        Date now = new Date();
        Date validBeginTime = coupon.getValidBegin();
        Date validEndTime = coupon.getValidEnd();

        boolean valid = true;

        if (Objects.nonNull(validBeginTime)) {
            valid = now.after(validBeginTime);
        }

        if (Objects.nonNull(validEndTime)) {
            valid = now.before(validEndTime);
        }

        if (coupon.getDisable() == 1) {
            valid = false;
        }

        if (coupon.getCount() <= 0) {
            valid = false;
        }

        return valid;
    }

    @Override
    public int addCommMsgFreeCoupon(Long userId, int count) throws ApiException {
        Coupon coupon = couponRepository.findByUserIdAndName(userId, CommonFreeMsgCoupon);
        if (Objects.isNull(coupon)) {
            coupon = new Coupon();
            coupon.setUserId(userId);
            coupon.setType(10);
            coupon.setName(CommonFreeMsgCoupon);
            coupon.setCount(count);
        } else {
            coupon.setCount(count + coupon.getCount());
        }

        couponRepository.save(coupon);

        return 1;
    }

    @Override
    public int addCommVoiceCoupon(Long userId, int count) throws ApiException {
        Coupon coupon = couponRepository.findByUserIdAndName(userId, CommonFreeVoiceCoupon);
        if (Objects.isNull(coupon)) {
            coupon = new Coupon();
            coupon.setUserId(userId);
            coupon.setType(20);
            coupon.setName(CommonFreeVoiceCoupon);
            coupon.setCount(count);
        } else {
            coupon.setCount(count + coupon.getCount());
        }

        couponRepository.save(coupon);

        return 1;
    }

    @Override
    public int addCommVideoCoupon(Long userId, int count) throws ApiException {
        Coupon coupon = couponRepository.findByUserIdAndName(userId, CommonFreeVideoCoupon);
        if (Objects.isNull(coupon)) {
            coupon = new Coupon();
            coupon.setUserId(userId);
            coupon.setType(30);
            coupon.setName(CommonFreeVideoCoupon);
            coupon.setCount(count);
        } else {
            coupon.setCount(count + coupon.getCount());
        }

        couponRepository.save(coupon);

        return 1;
    }
}
