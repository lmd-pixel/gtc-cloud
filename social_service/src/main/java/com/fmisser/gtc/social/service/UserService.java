package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    ApiResp<User> create(String phone, int gender) throws ApiException;
    ApiResp<User> profile(String username) throws ApiException;
    ApiResp<User> updateProfile(String username, String nick, String birth, String city,
                                String profession, String intro, String labels, String callPrice, String videoPrice,
                                Map<String, MultipartFile> multipartFileMap) throws ApiException;
    ApiResp<User> updatePhotos(String username, Map<String, MultipartFile> multipartFileMap) throws ApiException;
    ApiResp<User> updateVerifyImage(String username, Map<String, MultipartFile> multipartFileMap) throws ApiException;
    ApiResp<String> logout() throws ApiException;
}
