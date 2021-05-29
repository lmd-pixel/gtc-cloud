package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.aop.ReTry;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.domain.IdentityAudit;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.UserMaterial;
import com.fmisser.gtc.social.repository.IdentityAuditRepository;
import com.fmisser.gtc.social.repository.UserRepository;
import com.fmisser.gtc.social.service.IdentityAuditService;
import com.fmisser.gtc.social.service.UserMaterialService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class IdentityAuditServiceImpl implements IdentityAuditService {

    private final UserRepository userRepository;
    private final IdentityAuditRepository identityAuditRepository;
    private final UserMaterialService userMaterialService;

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
    public int requestIdentityAudit(User user, int type, int mode) throws ApiException {
        if (type == 1) {
            if (!_checkProfileCompleted(user) ||
                    !_checkPhotosCompleted(user) ||
                    !_checkVideoCompleted(user)) {
                throw new ApiException(-1, "资料尚未完善，请先完善资料再提交审核");
            }
        }

//        if (user.getIdentity() == 1) {
//            throw new ApiException(-1, "已完成了所有认证，无需再次认证");
//        }

        // 资料审核
        if (mode == 0 || mode == 1) {
            Optional<IdentityAudit> optionalIdentityAudit = getLastProfileAudit(user);
            if (optionalIdentityAudit.isPresent() &&
                    optionalIdentityAudit.get().getStatus() == 10) {
                throw new ApiException(-1, "用户资料仍在审核中，无法再次提交");
            } else {
//            IdentityAudit identityAudit = _createIdentityAudit(user, 1);
//            identityAuditRepository.save(identityAudit);

                IdentityAudit identityAudit = _createProfileAuditFromPrepare(user, 1);
                if (Objects.nonNull(identityAudit)) {
                    identityAuditRepository.save(identityAudit);
                }
            }
        }


        if (mode == 0 || mode == 2) {
            // 照片审核
            Optional<IdentityAudit> optionalIdentityAudit = getLastPhotosAudit(user);
            if (optionalIdentityAudit.isPresent() &&
                    optionalIdentityAudit.get().getStatus() == 10) {
                throw new ApiException(-1, "用户照片仍在审核中，无法再次提交");
            } else {
//            IdentityAudit identityAudit = _createIdentityAudit(user, 2);
//            identityAuditRepository.save(identityAudit);

                IdentityAudit identityAudit = _createPhotosAuditFromPrepare(user, 2);
                if (Objects.nonNull(identityAudit)) {
                    identityAuditRepository.save(identityAudit);
                }
            }
        }

        if (mode == 0 || mode == 3) {
            // 视频审核
            Optional<IdentityAudit> optionalIdentityAudit = getLastVideoAudit(user);
            if (optionalIdentityAudit.isPresent() &&
                    optionalIdentityAudit.get().getStatus() == 10) {
                throw new ApiException(-1, "用户视频仍在审核中，无法再次提交");
            } else {
//            IdentityAudit identityAudit = _createIdentityAudit(user, 3);
//            identityAuditRepository.save(identityAudit);

                IdentityAudit identityAudit = _createVideoAuditFromPrepare(user, 3);
                if (Objects.nonNull(identityAudit)) {
                    identityAuditRepository.save(identityAudit);
                }
            }
        }

        return 1;
    }

    @Override
    public int requestIdentityAuditEx(User user, int type, int mode) throws ApiException {
        if (type == 1) {
            if (!_checkProfileCompleted(user) ||
                    !_checkPhotosCompleted(user) ||
                    !_checkVideoCompleted(user)) {
                throw new ApiException(-1, "资料尚未完善，请先完善资料再提交审核");
            }
        }

//        if (user.getIdentity() == 1) {
//            throw new ApiException(-1, "已完成了所有认证，无需再次认证");
//        }

        // 资料审核
        if (mode == 0 || mode == 1) {
            Optional<IdentityAudit> optionalIdentityAudit = getLastProfileAudit(user);
            if (optionalIdentityAudit.isPresent() &&
                    optionalIdentityAudit.get().getStatus() == 10) {
                throw new ApiException(-1, "用户资料仍在审核中，无法再次提交");
            } else {
//            IdentityAudit identityAudit = _createIdentityAudit(user, 1);
//            identityAuditRepository.save(identityAudit);

                IdentityAudit identityAudit = _createProfileAuditFromPrepare(user, 1);
                if (Objects.nonNull(identityAudit)) {
                    identityAuditRepository.save(identityAudit);

                    // 守护版本 创建照片审核
                    List<UserMaterial> auditPreparePhotos = userMaterialService.getAuditPreparePhotos(user);
                    List<UserMaterial> auditPhotos = auditPreparePhotos.stream()
                            .peek(userMaterial -> userMaterial.setType(12))
                            .collect(Collectors.toList());
                    userMaterialService.updateList(auditPhotos);
                }
            }
        }

        if (mode == 0 || mode == 2) {
            // 照片审核
            Optional<IdentityAudit> optionalIdentityAudit = getLastPhotosAudit(user);
            if (optionalIdentityAudit.isPresent() &&
                    optionalIdentityAudit.get().getStatus() == 10) {
                throw new ApiException(-1, "用户照片仍在审核中，无法再次提交");
            } else {
//            IdentityAudit identityAudit = _createIdentityAudit(user, 2);
//            identityAuditRepository.save(identityAudit);

                IdentityAudit identityAudit = _createPhotosAuditFromPrepare(user, 2);
                if (Objects.nonNull(identityAudit)) {
                    identityAuditRepository.save(identityAudit);
                }
            }
        }

        if (mode == 0 || mode == 3) {
            // 视频审核
            Optional<IdentityAudit> optionalIdentityAudit = getLastVideoAudit(user);
            if (optionalIdentityAudit.isPresent() &&
                    optionalIdentityAudit.get().getStatus() == 10) {
                throw new ApiException(-1, "用户视频仍在审核中，无法再次提交");
            } else {
//            IdentityAudit identityAudit = _createIdentityAudit(user, 3);
//            identityAuditRepository.save(identityAudit);

                IdentityAudit identityAudit = _createVideoAuditFromPrepare(user, 3);
                if (Objects.nonNull(identityAudit)) {
                    identityAuditRepository.save(identityAudit);
                }
            }
        }

        return 1;
    }

    @Override
    public List<IdentityAudit> getLatestAuditAllType(User user) throws ApiException {
        return identityAuditRepository.getLatestWithAllType(user.getId());
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

    @Override
    public Optional<IdentityAudit> getLastProfilePrepare(User user) throws ApiException {
        return identityAuditRepository
                .findTopByUserIdAndTypeOrderByCreateTimeDesc(user.getId(), 11);
    }

    @Override
    public Optional<IdentityAudit> getLastPhotosPrepare(User user) throws ApiException {
        return identityAuditRepository
                .findTopByUserIdAndTypeOrderByCreateTimeDesc(user.getId(), 12);
    }

    @Override
    public Optional<IdentityAudit> getLastVideoPrepare(User user) throws ApiException {
        return identityAuditRepository
                .findTopByUserIdAndTypeOrderByCreateTimeDesc(user.getId(), 13);
    }

    @Override
    public Optional<IdentityAudit> getLastAuditPrepare(Long userId, int type) throws ApiException {
        return identityAuditRepository
                .findTopByUserIdAndTypeOrderByCreateTimeDesc(userId, type);
    }

    @Override
    public IdentityAudit createAuditPrepare(User user, int type) throws ApiException {

        // 如果还存在，并且状态是待审核 则一直使用这份待审核数据
        Optional<IdentityAudit> identityAuditOptional = identityAuditRepository
                .findTopByUserIdAndTypeOrderByCreateTimeDesc(user.getId(), type);

        if (identityAuditOptional.isPresent()) {
            if (identityAuditOptional.get().getStatus() == 10) {
                return identityAuditOptional.get();
            }
        }

        // 创建一份新的
        IdentityAudit audit = new IdentityAudit();
        audit.setSerialNumber(createAuditSerialNumber(user.getId(), type));
        audit.setUserId(user.getId());
        audit.setDigitId(user.getDigitId());
        audit.setGender(user.getGender());
        audit.setPhone(user.getPhone());
        audit.setType(type);
        audit.setStatus(10);
        return audit;
    }

    /**
     * 判断资料是否已全部完善
     */
    private boolean _checkProfileCompleted(User user) {

//        return !StringUtils.isEmpty(user.getHead()) &&
////                !StringUtils.isEmpty(user.getVoice()) &&
//                !StringUtils.isEmpty(user.getLabels()) &&
//                !StringUtils.isEmpty(user.getProfession()) &&
//                !StringUtils.isEmpty(user.getIntro()) &&
//                !StringUtils.isEmpty(user.getCity()) &&
//                user.getVideoPrice() != null &&
//                user.getCallPrice() != null &&
//                user.getMessagePrice() != null;

        Optional<IdentityAudit> identityAuditOptional = getLastProfilePrepare(user);
        if (!identityAuditOptional.isPresent()) {
            return false;
        }
        IdentityAudit identityAudit = identityAuditOptional.get();

        return
                (!StringUtils.isEmpty(identityAudit.getHead()) || !StringUtils.isEmpty(user.getHead())) &&
                        (!StringUtils.isEmpty(identityAudit.getVoice()) || !StringUtils.isEmpty(user.getVoice())) &&
                        (!StringUtils.isEmpty(identityAudit.getLabels()) || !StringUtils.isEmpty(user.getLabels())) &&
                        (!StringUtils.isEmpty(identityAudit.getProfession()) || !StringUtils.isEmpty(user.getProfession())) &&
                        (!StringUtils.isEmpty(identityAudit.getIntro()) || !StringUtils.isEmpty(user.getIntro())) &&
                        (!StringUtils.isEmpty(identityAudit.getCity()) || !StringUtils.isEmpty(user.getCity())) &&
                        (identityAudit.getVideoPrice() != null || (user.getVideoPrice() != null)) &&
                        (identityAudit.getCallPrice() != null || (user.getCallPrice() != null));
//                identityAudit.getMessagePrice() != null;
    }

    /**
     * 检查照片是否已完善
     */
    private boolean _checkPhotosCompleted(User user) {
//        return !StringUtils.isEmpty(user.getPhotos());
        Optional<IdentityAudit> identityAuditOptional = getLastPhotosPrepare(user);
        if (!identityAuditOptional.isPresent()) {
            return false;
        }
        IdentityAudit identityAudit = identityAuditOptional.get();
        return !StringUtils.isEmpty(identityAudit.getPhotos());
    }

    private boolean _checkVideoCompleted(User user) {
//        return !StringUtils.isEmpty(user.getVideo());
        Optional<IdentityAudit> identityAuditOptional = getLastVideoPrepare(user);
        if (!identityAuditOptional.isPresent()) {
            return false;
        }
        IdentityAudit identityAudit = identityAuditOptional.get();
        return !StringUtils.isEmpty(identityAudit.getVideo());
    }

    // 创建审核编号
    private String createAuditSerialNumber(long userId, int type) {
        // 编号号 = 当前时间戳 + 用户id（格式化成10位） + type 格式话成两位
        return String.format("%d%010d%02d",
                new Date().getTime(),
                userId,
                type);
    }

    // 审核的数据现在不从user信息拿
    @Deprecated
    private IdentityAudit _createIdentityAudit(User user, int type) {
        IdentityAudit audit = new IdentityAudit();
        audit.setSerialNumber(createAuditSerialNumber(user.getId(), type));
        audit.setUserId(user.getId());
        audit.setDigitId(user.getDigitId());
        audit.setNick(user.getNick());
        audit.setGender(user.getGender());
        audit.setPhone(user.getPhone());
        audit.setAge(DateUtils.getAgeFromBirth(user.getBirth()));
        audit.setType(type);
        audit.setStatus(10);
        return audit;
    }

    private IdentityAudit _createProfileAuditFromPrepare(User user, int type) {

        Optional<IdentityAudit> identityAuditOptional = getLastProfilePrepare(user);
        if (!identityAuditOptional.isPresent()) {
            return null;
        }
        IdentityAudit identityAuditPrepare = identityAuditOptional.get();
        if (identityAuditPrepare.getStatus() != 10) {
            // 状态不是待审核的则无效
            return null;
        }

        // 直接修改type
//        identityAuditPrepare.setType(type);
//        return identityAuditRepository.save(identityAuditPrepare);
        // 创建一个对应的审核
        return __innerCreateFromAuditPrepare(identityAuditPrepare, user, type);
    }

    private IdentityAudit _createPhotosAuditFromPrepare(User user, int type) {

        Optional<IdentityAudit> identityAuditOptional = getLastPhotosPrepare(user);
        if (!identityAuditOptional.isPresent()) {
            return null;
        }
        IdentityAudit identityAuditPrepare = identityAuditOptional.get();
        if (identityAuditPrepare.getStatus() != 10) {
            // 状态不是待审核的则无效
            return null;
        }

        // 直接修改type
//        identityAuditPrepare.setType(type);
//        return identityAuditRepository.save(identityAuditPrepare);
        // 创建一个对应的审核
        return __innerCreateFromAuditPrepare(identityAuditPrepare, user, type);
    }

    private IdentityAudit _createVideoAuditFromPrepare(User user, int type) {

        Optional<IdentityAudit> identityAuditOptional = getLastVideoPrepare(user);
        if (!identityAuditOptional.isPresent()) {
            return null;
        }
        IdentityAudit identityAuditPrepare = identityAuditOptional.get();
        if (identityAuditPrepare.getStatus() != 10) {
            // 状态不是待审核的则无效
            return null;
        }

        // 直接修改type
//        identityAuditPrepare.setType(type);
//        return identityAuditRepository.save(identityAuditPrepare);
        // 创建一个对应的审核
        return __innerCreateFromAuditPrepare(identityAuditPrepare, user, type);
    }

    private IdentityAudit __innerCreateFromAuditPrepare(IdentityAudit identityAuditPrepare, User user, int type) {
        IdentityAudit audit = new IdentityAudit();
        audit.setSerialNumber(createAuditSerialNumber(user.getId(), type));
        audit.setUserId(user.getId());
        audit.setDigitId(user.getDigitId());
        audit.setGender(user.getGender());
        audit.setPhone(user.getPhone());

        audit.setNick(identityAuditPrepare.getNick());
        audit.setBirth(identityAuditPrepare.getBirth());
        if (Objects.nonNull(identityAuditPrepare.getBirth())) {
            audit.setAge(DateUtils.getAgeFromBirth(identityAuditPrepare.getBirth()));
        }
        audit.setCity((identityAuditPrepare.getCity()));
        audit.setProfession(identityAuditPrepare.getProfession());
        audit.setIntro(identityAuditPrepare.getIntro());
        audit.setLabels(identityAuditPrepare.getLabels());
        audit.setCallPrice(identityAuditPrepare.getCallPrice());
        audit.setVideoPrice(identityAuditPrepare.getVideoPrice());
        audit.setMessagePrice(identityAuditPrepare.getMessagePrice());
        audit.setVoice(identityAuditPrepare.getVoice());
        audit.setHead(identityAuditPrepare.getHead());
        audit.setPhotos(identityAuditPrepare.getPhotos());
        audit.setVideo(identityAuditPrepare.getVideo());

        audit.setType(type);
        audit.setStatus(10);
        return audit;
    }
}
