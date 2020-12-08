package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.aop.ReTry;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.IdentityAudit;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.IdentityAuditRepository;
import com.fmisser.gtc.social.repository.UserRepository;
import com.fmisser.gtc.social.service.IdentityAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class IdentityAuditServiceImpl implements IdentityAuditService {

    private final UserRepository userRepository;

    private final IdentityAuditRepository identityAuditRepository;

    public IdentityAuditServiceImpl(UserRepository userRepository,
                                    IdentityAuditRepository identityAuditRepository) {
        this.userRepository = userRepository;
        this.identityAuditRepository = identityAuditRepository;
    }

    @Override
    public Optional<IdentityAudit> getLastProfileAudit(User user) throws ApiException {
        return identityAuditRepository
                .findTopByUserIdAndTypeOrderByCreateTimeDesc(user.getId(), 1);
    }

    @Override
    public Optional<IdentityAudit> getLastPhotosAudit(User user) throws ApiException {
        return identityAuditRepository
                .findTopByUserIdAndTypeOrderByCreateTimeDesc(user.getId(), 2);
    }

    @Override
    public Optional<IdentityAudit> getLastVideoAudit(User user) throws ApiException {
        return identityAuditRepository
                .findTopByUserIdAndTypeOrderByCreateTimeDesc(user.getId(), 3);
    }

    @Override
    public int requestIdentityAudit(User user) throws ApiException {
        if (!_checkProfileCompleted(user) ||
                !_checkPhotosCompleted(user) ||
                !_checkVideoCompleted(user)) {
            throw new ApiException(-1, "资料尚未完善，请先完善资料再提交审核");
        }

        if (user.getIdentity() == 1) {
            throw new ApiException(-1, "已完成了所有认证，无需再次认证");
        }

        // 资料审核
        Optional<IdentityAudit> optionalIdentityAudit = getLastProfileAudit(user);
        if (optionalIdentityAudit.isPresent() &&
                optionalIdentityAudit.get().getStatus() == 10) {
            throw new ApiException(-1, "用户资料仍在审核中，无法再次提交");
        } else {
            IdentityAudit audit = new IdentityAudit();
            audit.setUserId(user.getId());
            audit.setType(1);
            audit.setStatus(10);
            identityAuditRepository.save(audit);
        }

        // 照片审核
        optionalIdentityAudit = getLastPhotosAudit(user);
        if (optionalIdentityAudit.isPresent() &&
                optionalIdentityAudit.get().getStatus() == 10) {
            throw new ApiException(-1, "用户照片仍在审核中，无法再次提交");
        } else {
            IdentityAudit audit = new IdentityAudit();
            audit.setUserId(user.getId());
            audit.setType(2);
            audit.setStatus(10);
            identityAuditRepository.save(audit);
        }

        // 视频审核
        optionalIdentityAudit = getLastVideoAudit(user);
        if (optionalIdentityAudit.isPresent() &&
                optionalIdentityAudit.get().getStatus() == 10) {
            throw new ApiException(-1, "用户视频仍在审核中，无法再次提交");
        } else {
            IdentityAudit audit = new IdentityAudit();
            audit.setUserId(user.getId());
            audit.setType(3);
            audit.setStatus(10);
            identityAuditRepository.save(audit);
        }

        return 1;
    }

    /**
     * 审核身份认证
     * @deprecated 已转移到 {@link com.fmisser.gtc.social.service.UserManagerService}
     */
    @Deprecated
    @Transactional
//    @ReTry(value = {PessimisticLockingFailureException.class})
//    @Retryable(value = {PessimisticLockingFailureException.class})
    @Override
    public int responseAudit(Long auditId, int pass, String message) throws ApiException {
        Optional<IdentityAudit> identityAuditOptional = identityAuditRepository.findById(auditId);
        if (!identityAuditOptional.isPresent()) {
            throw new ApiException(-1, "无效审核数据");
        }

        IdentityAudit identityAudit = identityAuditOptional.get();
        if (identityAudit.getStatus() >= 20) {
            throw new ApiException(-1, "该条审核已完成");
        }

        if (pass == 1) {
            // 通过
            identityAudit.setStatus(30);
        } else {
            // 未通过
            identityAudit.setStatus(20);
        }

        if (!message.isEmpty()) {
            identityAudit.setMessage(message);
        }

        identityAuditRepository.save(identityAudit);

        // 判断是否不同的审核都已满足,如果都通过，则完成了认证
        if (pass == 1) {
//            int type = identityAudit.getType() == 1 ? 2 : 1;
//            Optional<IdentityAudit> identityAuditAnother = identityAuditRepository
//                    .findTopByUserIdAndTypeOrderByCreateTimeDesc(identityAudit.getUserId(), type);
//
//            if (identityAuditAnother.isPresent() &&
//                    identityAuditAnother.get().getStatus() == 30) {
//                // 两个都通过认证，则认为身份认证通过, 更新用户信息里的身份认证字段
//                Optional<User> user = userRepository.findById(identityAudit.getUserId());
//                user.get().setIdentity(1);
//                userRepository.save(user.get());
//            }

            boolean allPass = true;
            for (int type = 0; type < 3; type++) {
                if (type == identityAudit.getType()) {
                    continue;
                }

                Optional<IdentityAudit> identityAuditOther = identityAuditRepository
                    .findTopByUserIdAndTypeOrderByCreateTimeDesc(identityAudit.getUserId(), type);

                if (identityAuditOther.isPresent() &&
                        identityAuditOther.get().getStatus() != 30) {
                    allPass = false;
                }
            }

            if (allPass) {
                // 都通过认证，则认为身份认证通过, 更新用户信息里的身份认证字段
                Optional<User> user = userRepository.findById(identityAudit.getUserId());
                user.get().setIdentity(1);
                userRepository.save(user.get());
            }
        }

        return 1;
    }

    /**
     * 判断资料是否已全部完善
     */
    private boolean _checkProfileCompleted(User user) {
        return !StringUtils.isEmpty(user.getHead()) &&
                !StringUtils.isEmpty(user.getVoice()) &&
                !StringUtils.isEmpty(user.getLabels()) &&
                !StringUtils.isEmpty(user.getProfession()) &&
                !StringUtils.isEmpty(user.getIntro()) &&
                !StringUtils.isEmpty(user.getCity()) &&
                user.getVideoPrice() != null &&
                user.getCallPrice() != null;
    }

    /**
     * 检查照片是否已完善
     */
    private boolean _checkPhotosCompleted(User user) {
        return !StringUtils.isEmpty(user.getPhotos());
    }

    private boolean _checkVideoCompleted(User user) {
        return !StringUtils.isEmpty(user.getVideo());
    }
}
