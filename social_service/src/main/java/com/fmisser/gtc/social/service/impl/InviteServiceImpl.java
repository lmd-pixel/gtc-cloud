package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Invite;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.InviteRepository;
import com.fmisser.gtc.social.service.InviteService;
import com.fmisser.gtc.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class InviteServiceImpl implements InviteService {

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private UserService userService;

    @Override
    public int inviteFromDigitId(User user, String inviteCode) throws ApiException {
        Invite invite = inviteRepository.findByInvitedUserId(user.getId());
        if (Objects.nonNull(invite)) {
            throw new ApiException(-1, "用户已有邀请人信息，无法继续绑定");
        }

        User fromUser = userService.getUserByDigitId(inviteCode);
        if (Objects.isNull(fromUser)) {
            throw new ApiException(-1, "邀请码无效");
        }

        Invite newInvite = new Invite();
        newInvite.setUserId(fromUser.getId());
        newInvite.setInvitedUserId(user.getId());
        newInvite.setInviteCode(inviteCode);
        newInvite.setType(0);

        inviteRepository.save(newInvite);

        return 1;
    }

    @Override
    public User getInviteUserFromDigitId(String inviteCode) throws ApiException {
        User fromUser = userService.getUserByDigitId(inviteCode);
        if (Objects.isNull(fromUser)) {
            throw new ApiException(-1, "邀请码无效");
        }
        return fromUser;
    }
}
