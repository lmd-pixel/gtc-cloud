package com.fmisser.fpp.thirdparty.jpush.service;

/**
 * @author fmisser
 * @create 2021-05-13 下午3:29
 * @description
 */
public interface JPushService {

    /**
     * 认证login token，成功返回手机号
     */
    String verifyLoginToken(String identity, String loginToken) throws RuntimeException;
}
