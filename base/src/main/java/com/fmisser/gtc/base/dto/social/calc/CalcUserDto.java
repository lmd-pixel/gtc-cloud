package com.fmisser.gtc.base.dto.social.calc;

/**
 * 统计用户数据
 */
public interface CalcUserDto {
    // 活跃用户
    Long getActiveUser();

    // 新用户
    Long getNewUser();

    // 新主播
    Long getAnchorUser();

    // 新充值用户
    Long getNewRecharge();

    // 总充值用户
    Long getTotalRecharge();

    // 新消费
    Long getNewConsume();

    // 总消费
    Long getTotalConsume();

    // 总提现用户
    Long getTotalWithdraw();
}
