package com.fmisser.gtc.auth.service.impl;

import com.fmisser.fpp.thirdparty.jpush.service.JPushService;
import com.fmisser.gtc.auth.domain.Role;
import com.fmisser.gtc.auth.domain.User;
import com.fmisser.gtc.auth.feign.OAuthFeign;
import com.fmisser.gtc.auth.repository.RoleRepository;
import com.fmisser.gtc.auth.repository.UserRepository;
import com.fmisser.gtc.auth.service.AutoLoginService;
import com.fmisser.gtc.auth.service.SmsService;
import com.fmisser.gtc.auth.service.ThirdPartyLoginService;
import com.fmisser.gtc.auth.service.UserService;
import com.fmisser.gtc.base.dto.auth.TokenDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OauthConfProp;
import com.fmisser.gtc.base.utils.AuthUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("top")
public class UserDetailServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserDetailServiceImpl.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final SmsService smsService;

    private final AutoLoginService autoLoginService;

    private final OAuthFeign oAuthFeign;

    private final ThirdPartyLoginService thirdPartyLoginService;

    private final OauthConfProp oauthConfProp;

    private final JPushService jPushService;

    public UserDetailServiceImpl(UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 PasswordEncoder passwordEncoder,
                                 SmsService smsService,
                                 AutoLoginService autoLoginService,
                                 OAuthFeign oAuthFeign,
                                 ThirdPartyLoginService thirdPartyLoginService,
                                 OauthConfProp oauthConfProp,
                                 JPushService jPushService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.smsService = smsService;
        this.autoLoginService = autoLoginService;
        this.oAuthFeign = oAuthFeign;
        this.thirdPartyLoginService = thirdPartyLoginService;
        this.oauthConfProp = oauthConfProp;
        this.jPushService = jPushService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s);
    }

    @Override
    public User create(String username, String password, String roleName) {
        // TODO: 2020/11/21 通过jpa函数调用是否需要防sql注入
        roleName = StringEscapeUtils.escapeSql(roleName);

        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        userOptional.ifPresent(u -> {
//            throw new IllegalArgumentException("user already exists:" + u.getUsername());
            throw new ApiException(-1, "用户已存在");
        });

        User user = new User();
        user.setId(0L);
        user.setUsername(username);
        String encodePwd = passwordEncoder.encode(password);
        user.setPassword(encodePwd);

        if (roleName == null || roleName.isEmpty()) {
            // give default role: USER_ROLE
            user.setAuthorities(_innerCreateUserRole());
        } else {
            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new ApiException(-1, "不存在该权限");
            }
            user.setAuthorities(_innerCreteRole(role));
        }

        User savedUser = userRepository.save(user);
        logger.info("new user has been created: {}", savedUser.getUsername());
        return savedUser;
    }

    @Override
    public User editUser(String username, String password, String roleName) throws ApiException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (!userOptional.isPresent()) {
            throw new ApiException(-1, "用户不存在存在");
        }

        User user = userOptional.get();

        if (Objects.nonNull(password)) {
            String encodePwd = passwordEncoder.encode(password);
            user.setPassword(encodePwd);
        }

        if (Objects.nonNull(roleName)) {
//            List<String> roleList = user.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//            if (roleList.contains(roleName)) {
//                throw new ApiException(-1, "用户角色已存在");
//            }

            Role role = roleRepository.findByName(roleName);
            if (role == null) {
                throw new ApiException(-1, "不存在该权限");
            }

            user.setAuthorities(_innerCreteRole(role));
        }

        User savedUser = userRepository.save(user);
        logger.info("user has been updated: {}", savedUser.getUsername());
        return savedUser;
    }

    @Override
    public int enableUser(String username, int enable) throws ApiException {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (!userOptional.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }

        User user = userOptional.get();
        user.setEnabled(enable);
        userRepository.save(user);

        return 1;
    }

    @Override
    public int deleteUser(String username) throws ApiException {
//        throw new ApiException(-1,"危险操作，已禁止");
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
        if (!userOptional.isPresent()) {
            throw new ApiException(-1, "用户不存在");
        }

        User user = userOptional.get();
        user.setLocked(1);
        userRepository.save(user);

        return 1;
    }

    @Override
    public TokenDto autoLogin(String identity, String token) throws ApiException {
        String basicAuth = AuthUtils.genBasicAuthString(oauthConfProp.getOauth2Client(), oauthConfProp.getOauth2ClientSecret());
        return oAuthFeign.autoLogin(basicAuth, identity, token, oauthConfProp.getOauth2Scope(), "auto_login");
    }

    @Override
    public TokenDto smsLogin(String phone, String code) throws ApiException {
        String basicAuth = AuthUtils.genBasicAuthString(oauthConfProp.getOauth2Client(), oauthConfProp.getOauth2ClientSecret());
        return oAuthFeign.smsLogin(basicAuth, phone, code, oauthConfProp.getOauth2Scope(), "sms_login");
    }

    @Override
    public TokenDto login(String username, String password) throws ApiException {
        String basicAuth = AuthUtils.genBasicAuthString(oauthConfProp.getOauth2Client(), oauthConfProp.getOauth2ClientSecret());
        return oAuthFeign.login(basicAuth, username, password, oauthConfProp.getOauth2Scope(), "password");
    }

    @Override
    public TokenDto appleLogin(String subject, String token) throws ApiException {
        String basicAuth = AuthUtils.genBasicAuthString(oauthConfProp.getOauth2Client(), oauthConfProp.getOauth2ClientSecret());
        return oAuthFeign.appleLogin(basicAuth, subject, token, oauthConfProp.getOauth2Scope(), "apple_login");
    }

    @Override
    public TokenDto refreshToken(String refreshToken) throws ApiException {
        String basicAuth = AuthUtils.genBasicAuthString(oauthConfProp.getOauth2Client(), oauthConfProp.getOauth2ClientSecret());
        return oAuthFeign.refreshToken(basicAuth, refreshToken, "refresh_token");
    }

    /**
     * 自定义手机号验证码登录模式
     * 注册，登录一体
     */
    public UserDetails loadUserByPhoneAndSms(String phone, String code) {
        // verify sms code
        if (!smsService.checkPhoneCode(phone, code, 0)) {
            return null;
        }

        User user = userRepository.findByUsername(phone);
        if (user != null) {
            return user;
        }

        // 认证过后，如果数据库没有这条数据，则创建一条
        return _innerCreateByUsername(phone, 1);
    }

    /**
     * 自定义手机号一键登录模式
     * 注册，登录一体
     * @param identity
     */
    public UserDetails loadUserByPhoneAuto(String identity, String token) {
        // verify phone token

        String phoneDecode;
        if (StringUtils.isEmpty(identity)) {
            // 兼容老版本
            phoneDecode = autoLoginService.checkPhoneToken("", token);
        } else {
            phoneDecode =  jPushService.verifyLoginToken(identity, token);
        }

        if (phoneDecode.isEmpty()) {
            return null;
        }

        User user = userRepository.findByUsername(phoneDecode);
        if (user != null) {
            return user;
        }

        // 认证过后，如果数据库没有这条数据，则创建一条
        return _innerCreateByUsername(phoneDecode, 2);
    }

    /**
     * 自定义苹果一键登录
     * 注册。登录一体
     */
    public UserDetails loadUserByAppleAuto(String subject, String token) {
        if (!thirdPartyLoginService.checkAppleIdentityToken(token, subject)) {
            return null;
        }

        User user = userRepository.findByUsername(subject);
        if (user != null) {
            return user;
        }

        // 认证过后，如果数据库没有这条数据，则创建一条
        return _innerCreateByUsername(subject, 11);
    }

    @Override
    public int logout(User user) throws ApiException {
        return 0;
    }

    private User _innerCreateByUsername(String username, int type) {
        User newUser = new User();
        newUser.setId(0L);  // 因为存在多对多，必须要设置个值
        newUser.setUsername(username);
        newUser.setType(type); // 类型
        String encodePwd = passwordEncoder.encode(new Date().toString());   // 随机创建一个密码
        newUser.setPassword(encodePwd);

        // give default role: USER_ROLE
        newUser.setAuthorities(_innerCreateUserRole());

        User savedUser = userRepository.save(newUser);
        logger.info("new user has been created: {}", savedUser.getUsername());
        return savedUser;
    }

    private List<Role> _innerCreateUserRole() {
        Role userRole = roleRepository.getUserRole();
        List<Role> roleList = new ArrayList<>();
        roleList.add(userRole);

        return roleList;
    }

    private List<Role> _innerCreteRole(Role role) {
        List<Role> roleList = new ArrayList<>();
        roleList.add(role);

        return roleList;
    }
}
