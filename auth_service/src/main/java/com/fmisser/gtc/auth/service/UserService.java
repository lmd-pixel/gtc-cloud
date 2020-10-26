package com.fmisser.gtc.auth.service;

import com.fmisser.gtc.auth.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    /**
     * 账户密码注册
     */
    User create(String username, String password);
}
