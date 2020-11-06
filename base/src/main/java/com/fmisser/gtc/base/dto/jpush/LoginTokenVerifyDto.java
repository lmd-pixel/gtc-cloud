package com.fmisser.gtc.base.dto.jpush;

import lombok.Data;

/**
 * 手机号一键登录返回数据:
 * http://docs.jiguang.cn/jverification/server/rest_api/loginTokenVerify_api/
 */

@Data
public class LoginTokenVerifyDto {
    private Long id;
    private int code;
    private String exID;
    private String content;
    private String phone;
}
