package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {
    User create(String phone, int gender) throws ApiException;
    User profile(String username) throws ApiException;
    User updateProfile(String username, String nick, String birth, String city,
                                String profession, String intro, String labels, String callPrice, String videoPrice,
                                Map<String, MultipartFile> multipartFileMap) throws ApiException;
    User updatePhotos(String username, Map<String, MultipartFile> multipartFileMap) throws ApiException;
    User updateVerifyImage(String username, Map<String, MultipartFile> multipartFileMap) throws ApiException;
    int logout() throws ApiException;
}
