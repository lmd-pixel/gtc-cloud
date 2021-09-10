package com.fmisser.gtc.auth.service.impl;

import com.fmisser.fpp.thirdparty.apple.service.AppleIdLoginService;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Slf4j
@Service("top")
@AllArgsConstructor
public class UserDetailServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;
    private final AutoLoginService autoLoginService;
    private final OAuthFeign oAuthFeign;
    private final ThirdPartyLoginService thirdPartyLoginService;
    private final OauthConfProp oauthConfProp;
    private final JPushService jPushService;
    private final AppleIdLoginService appleIdLoginService;
    private final RedisTemplate redisTemplate;


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
        log.info("new user has been created: {}", savedUser.getUsername());
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
        log.info("user has been updated: {}", savedUser.getUsername());
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
    public TokenDto autoLogin(String identity, String token,String ipAddress,String deviceId) throws ApiException {
        String basicAuth = AuthUtils.genBasicAuthString(oauthConfProp.getOauth2Client(), oauthConfProp.getOauth2ClientSecret());
        if((!StringUtils.isEmpty(ipAddress) && ipAddress!="" && ipAddress!=null) || (deviceId!=null && !StringUtils.isEmpty(deviceId) && deviceId!=null)){
        /*    Set<String> keys_ip = redisTemplate.keys("*:" + ipAddress);
            List<String> ipList = new ArrayList<String>();
            List<Object> results = redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    for (String s : keys_ip) {
                        String ipAdress = (String) redisTemplate.opsForValue().get(s);
                        ipList.add(ipAdress);
                    }
                    return null;
                }
            });
            Set<String> keys_device = redisTemplate.keys("*:" + deviceId);
            List<String> deviceList = new ArrayList<String>();
            List<Object> results2 = redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    for (String s : keys_device) {
                        String device = (String) redisTemplate.opsForValue().get(s);
                        deviceList.add(device);
                    }
                    return null;
                }
            });*/
            if(redisTemplate.hasKey(ipAddress) ||redisTemplate.hasKey(deviceId)){
                throw new ApiException(-1, "LOGINFAIL");
            }else{
                return oAuthFeign.autoLogin(basicAuth, identity, token, oauthConfProp.getOauth2Scope(), "auto_login");
            }

        }else{
            return oAuthFeign.autoLogin(basicAuth, identity, token, oauthConfProp.getOauth2Scope(), "auto_login");
        }

    }

    @Override
    public TokenDto smsLogin(String phone, String code,String ipAddress,String device) throws ApiException {
        String basicAuth = AuthUtils.genBasicAuthString(oauthConfProp.getOauth2Client(), oauthConfProp.getOauth2ClientSecret());
        if((!StringUtils.isEmpty(ipAddress) && ipAddress!="" ) || (device!=null && !StringUtils.isEmpty(device))){
            if(redisTemplate.hasKey(ipAddress) ||redisTemplate.hasKey(device)){
                throw new ApiException(-1, "LOGINFAIL");
            }else{
                return oAuthFeign.smsLogin(basicAuth, phone, code, oauthConfProp.getOauth2Scope(), "sms_login");
            }
        }else{
            return oAuthFeign.smsLogin(basicAuth, phone, code, oauthConfProp.getOauth2Scope(), "sms_login");
        }

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
    public TokenDto gooleLogin(String code, String token) throws ApiException {
        String basicAuth = AuthUtils.genBasicAuthString(oauthConfProp.getOauth2Client(), oauthConfProp.getOauth2ClientSecret());
        return oAuthFeign.gooleLogin(basicAuth,code,token,oauthConfProp.getOauth2Scope(),"goole_login");
    }

    @Override
    public TokenDto wxLogin(String unionid) throws ApiException {
        String basicAuth = AuthUtils.genBasicAuthString(oauthConfProp.getOauth2Client(), oauthConfProp.getOauth2ClientSecret());
        return oAuthFeign.wxLogin(basicAuth, unionid, oauthConfProp.getOauth2Scope(), "wx_login");
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
    public UserDetails loadUserByPhoneAndSms (String phone, String code) {
        // verify sms code
        if (!smsService.checkPhoneCode(phone, code, 0)) {
            return null;
        }

        User user = userRepository.findByUsername(phone);
        if (user != null) {
         /* try{
              String key="user:forbidden:"+phone;
              if(Objects.nonNull(redisTemplate.opsForValue().get(key))){
                  throw new Exception("LOGINFAIL");
              }else{
                  return user;
              }
            }catch(Exception e){
             e.getStackTrace();
            }*/
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
            String key="user:forbidden:"+phoneDecode;
            try{
                if(Objects.nonNull(redisTemplate.opsForValue().get(key))){
                    throw new ApiException(-1, "你暂时无法登录");
                }else{
                    return user;
                }
            }catch(Exception e){
                throw  new ApiException(-1,e.getMessage());
            }
        }

        // 认证过后，如果数据库没有这条数据，则创建一条
        return _innerCreateByUsername(phoneDecode, 2);
    }

    /**
     * 自定义苹果一键登录
     * 注册。登录一体
     */
    public UserDetails loadUserByAppleAuto(String subject, String token) {
//        if (!thirdPartyLoginService.checkAppleIdentityToken(token, subject)) {
//            return null;
//        }

        if (!appleIdLoginService.verifyIdentityToken("", subject, token, false)) {
            return null;
        }

        User user = userRepository.findByUsername(subject);
        if (user != null) {
            return user;
        }

        // 认证过后，如果数据库没有这条数据，则创建一条
        return _innerCreateByUsername(subject, 11);
    }

    /**
     * 自定义苹果一键登录
     * 注册。登录一体
     */
    public UserDetails loadUserByWxLogin(String unionid) {
        if (!thirdPartyLoginService.checkWxLogin(unionid)) {
            return null;
        }

        User user = userRepository.findByUsername(unionid);
        if (user != null) {
            return user;
        }

        // 认证过后，如果数据库没有这条数据，则创建一条
        return _innerCreateByUsername(unionid, 12);
    }

    /***
     * 自定义谷歌登录
     * @param token
     * @return
     */
    public  UserDetails loaduserByGooleLogin(String code,String token) throws GeneralSecurityException, IOException {
        if (!thirdPartyLoginService.getGooleAccessTOken(code,token)) {
            return null;
        }

        User user = userRepository.findByUsername(code);
        if (user != null) {
            return user;
        }

        // 认证过后，如果数据库没有这条数据，则创建一条
        return _innerCreateByUsername(code, 22);
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
        log.info("new user has been created: {}", savedUser.getUsername());
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
