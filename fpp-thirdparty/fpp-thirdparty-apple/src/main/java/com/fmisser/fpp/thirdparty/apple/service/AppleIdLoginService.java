package com.fmisser.fpp.thirdparty.apple.service;

/**
 * @author fmisser
 * @create 2021-05-14 下午8:04
 * @description 苹果一键登录
 * 验证参考：https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/authenticating_users_with_sign_in_with_apple
 */
public interface AppleIdLoginService {
    /**
     * 验证identity token，这里只校验部分字段，比如过期时间等没有进行校验，完整的校验请参考官方文档
     * @param strict 是否使用严格模式，非严格模式：
     *               1. 从苹果获取auth keys的时候失败则不进行下一步的校验，直接认为验证通过
     *               2. 不校验 audience，subject
     *               如果需要很高的安全性，请使用严格模式
     */
    boolean verifyIdentityToken(String audience, String subject, String identityToken, boolean strict) throws RuntimeException;
}
