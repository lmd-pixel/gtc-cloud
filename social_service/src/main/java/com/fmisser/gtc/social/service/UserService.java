package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    User create(String phone, int gender) throws ApiException;
    User getUserByUsername(String username) throws ApiException;
    User getUserByDigitId(String digitId) throws ApiException;
    User profile(User user) throws ApiException;
    User updateProfile(User user,
                       String nick, String birth, String city,
                       String profession, String intro, String labels, String callPrice, String videoPrice,
                       Map<String, MultipartFile> multipartFileMap) throws ApiException;
    User updatePhotos(User user, Map<String, MultipartFile> multipartFileMap) throws ApiException;
    User updateVerifyImage(User user, Map<String, MultipartFile> multipartFileMap) throws ApiException;
    User updateVideo(User user, Map<String, MultipartFile> multipartFileMap) throws ApiException;
    int logout(User user) throws ApiException;
    List<User> getAnchorList(Integer type, Integer gender, int pageIndex, int pageSize) throws ApiException;
}
