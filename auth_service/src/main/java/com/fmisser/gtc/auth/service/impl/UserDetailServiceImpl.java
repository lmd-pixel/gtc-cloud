package com.fmisser.gtc.auth.service.impl;

import com.fmisser.gtc.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("top")
public class UserDetailServiceImpl implements UserDetailsService {

    @Resource
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s);
    }

    /**
     * 自定义手机号验证码登录模式
     */
    public UserDetails loadUserByPhoneAndSms(String phone, String code) {
        // TODO: 2020/10/24 verify sms code

        return userRepository.findTopByPhone(phone);
    }
}
