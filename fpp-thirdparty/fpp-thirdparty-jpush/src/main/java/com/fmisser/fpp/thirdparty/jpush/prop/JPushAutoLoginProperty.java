package com.fmisser.fpp.thirdparty.jpush.prop;

import lombok.Data;

/**
 * @author fmisser
 * @create 2021-05-13 下午10:46
 * @description 极光一键登录的属性配置
 */
@Data
public class JPushAutoLoginProperty {
    // android package name
    private String packageName;
    // ios bundle id
    private String bundleId;
    // public rsa
    private String rsaPub;
    // private rsa
    private String rsaPri;
}
