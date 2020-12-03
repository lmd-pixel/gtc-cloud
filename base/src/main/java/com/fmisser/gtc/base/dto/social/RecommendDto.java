package com.fmisser.gtc.base.dto.social;

/**
 * 推荐主播数据
 */

public interface RecommendDto {
    Long getLevel();
    int getType();
    String getDigitId();
    String getNick();
    String getPhone();
    int getGender();
}
