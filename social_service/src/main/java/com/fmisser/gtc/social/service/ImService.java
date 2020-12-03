package com.fmisser.gtc.social.service;

import com.fmisser.gtc.social.domain.User;

/**
 * im trtc 相关 服务
 */

public interface ImService {
    // 用户登陆
    Object login(User user);

    // 用户登出
    Object logout(User user);

    // 发起通话，返回需要的数据
    Object call(User userFrom, User userTo);

    // 接受通话
    Object accept(User userFrom, User userTo, Long roomId);

    // 挂断通话
    Object hangup(User userFrom, User userTo, Long roomId);

}