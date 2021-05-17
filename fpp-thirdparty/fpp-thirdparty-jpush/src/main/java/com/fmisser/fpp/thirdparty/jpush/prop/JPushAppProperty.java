package com.fmisser.fpp.thirdparty.jpush.prop;

import lombok.Data;

/**
 * @author fmisser
 * @create 2021-05-13 下午10:44
 * @description 应用配置
 */
@Data
public class JPushAppProperty {
    private String appKey;
    private String masterSecret;
    private JPushAutoLoginProperty autoLogin;
}
