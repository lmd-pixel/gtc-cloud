package com.fmisser.fpp.thirdparty.apple.dto;

import lombok.Data;

/**
 * @author fmisser
 * @create 2021-05-14 下午8:37
 * @description identity token 头部，本身是一个jwt 结构
 */
@Data
public class AppleIdLoginIdentityTokenHeader {
    private String alg;
    private String kid;
}
