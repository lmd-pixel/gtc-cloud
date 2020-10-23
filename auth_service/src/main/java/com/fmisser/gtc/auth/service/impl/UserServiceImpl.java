package com.fmisser.gtc.auth.service.impl;

import com.fmisser.gtc.auth.domain.Role;
import com.fmisser.gtc.auth.domain.User;
import com.fmisser.gtc.auth.repository.RoleRepository;
import com.fmisser.gtc.auth.repository.UserRepository;
import com.fmisser.gtc.auth.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserRepository userRepository;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public User create(User user) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));
        userOptional.ifPresent(u -> {
            throw new IllegalArgumentException("user already exists:" + u.getUsername());
        });

        String encodePwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePwd);

        // give default role: USER_ROLE
        Role userRole = roleRepository.getUserRole();
        List<Role> roleList = new ArrayList<>();
        roleList.add(userRole);
        user.setAuthorities(roleList);

        User savedUser = userRepository.save(user);
        logger.info("new user has been created: {}", savedUser.getUsername());
        return savedUser;
    }
}
