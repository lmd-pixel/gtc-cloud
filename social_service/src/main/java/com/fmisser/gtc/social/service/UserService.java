package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {

    // 创建用户
    User create(String phone, int gender, String inviteCode, String version) throws ApiException;

    // 通过用户名查找用户实体
    User getUserByUsername(String username) throws ApiException;

    boolean isUserExist(String username) throws ApiException;

    // 通过数字id查找用户实体
    User getUserByDigitId(String digitId) throws ApiException;

    User getUserById(Long id) throws ApiException;

    // 获取自己的完整信息
    User profile(User user) throws ApiException;

    User getSelfProfile(User user) throws ApiException;

    // 更新用户信息
    User updateProfile(User user, Integer updateType,
                       String nick, String birth, String city,
                       String profession, String intro, String labels, String callPrice, String videoPrice, String messagePrice,
                       Integer mode, Integer rest, String restStartDate, String restEndDate,
                       Map<String, MultipartFile> multipartFileMap) throws ApiException;

    // 更新用户照片
    User updatePhotos(User user, Integer updateType,
                      String existsPhotos, Map<String, MultipartFile> multipartFileMap) throws ApiException;

    // 更新用户认证照片
    User updateVerifyImage(User user, Map<String, MultipartFile> multipartFileMap) throws ApiException;

    // 更新用户视频
    User updateVideo(User user, Integer updateType, Map<String, MultipartFile> multipartFileMap) throws ApiException;

    // 用户登出
    int logout(User user) throws ApiException;

    // 获取主播列表
    List<User> getAnchorList(Integer type, Integer gender, int pageIndex, int pageSize) throws ApiException;

    List<User> getAuditAnchorList(Integer type, Integer gender, int pageIndex, int pageSize) throws ApiException;

    // 获取主播信息，包括和自己关联的信息
    User getAnchorProfile(User user, User selfUser) throws ApiException;

    // 随机寻找n个主播
    List<User> getRandAnchorList(int count) throws ApiException;

    // 通话前检查
    Integer callPreCheck(User fromUser, User toUser, int type) throws ApiException;
}
