package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.*;
import com.fmisser.gtc.base.dto.social.calc.CalcConsumeDto;
import com.fmisser.gtc.base.dto.social.calc.CalcTotalProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcUserDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.*;
import com.fmisser.gtc.social.repository.*;
import com.fmisser.gtc.social.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserManagerServiceImpl implements UserManagerService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RecommendRepository recommendRepository;
    private final IdentityAuditRepository identityAuditRepository;
    private final AssetRepository assetRepository;
    private final LabelRepository labelRepository;
    private final IdentityAuditService identityAuditService;
    private final ImService imService;
    private final AsyncService asyncService;
    private final UserMaterialService userMaterialService;

    @Override
    public Pair<List<AnchorDto>,Map<String, Object>> getAnchorList(String digitId, String nick, String phone, Integer gender,
                                         Date startTime, Date endTime,
                                         int pageIndex, int pageSize,
                                         int sortColumn, int sortDirection) throws ApiException {
//        Sort.Direction direction;
        String sortProp;
//        if (sortDirection == 0) {
//            direction = Sort.Direction.ASC;
//        } else {
//            direction = Sort.Direction.DESC;
//        }

        switch (sortColumn) {
            case 0:
                sortProp = "createTime";
                break;
            case 1:
                sortProp = "activeTime";
                break;
            case 2:
                sortProp = "coin";
                break;
            case 3:
                sortProp = "giftProfit";
                break;
            case 4:
                sortProp = "messageProfit";
                break;
            case 5:
                sortProp = "audioProfit";
                break;
            case 6:
                sortProp = "audioDuration";
                break;
            case 7:
                sortProp = "videoProfit";
                break;
            case 8:
                sortProp = "videoDuration";
                break;
            default:
                sortProp = "createTime";
                break;
        }

        if (sortDirection == 0) {
            sortProp += " ASC";
        } else {
            sortProp += " DESC";
        }

//        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, direction, sortProp);
//        Page<AnchorDto> anchorDtoPage = userRepository
//                .anchorStatistics(digitId, nick, phone, gender, startTime, endTime, pageable);

        List<AnchorDto> anchorDtoList = userRepository
                .anchorStatisticsEx2(digitId, nick, phone, gender, startTime, endTime, pageSize, (pageIndex - 1) * pageSize, sortProp);

        Long totalCount = userRepository.countAnchorStatisticsEx(digitId, nick, phone, gender, startTime, endTime);
        Long totalPage = (totalCount / pageSize) + 1;

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", totalPage);
        extra.put("totalEle", totalCount);
        extra.put("currPage", pageIndex);
        extra.put("totalUser", totalCount);

        return Pair.of(anchorDtoList, extra);
    }

    @Override
    public Pair<List<ConsumerDto>,Map<String, Object>> getConsumerList(String digitId, String nick, String phone,
                                             Date startTime, Date endTime,
                                             int pageIndex, int pageSize,
                                             int sortColumn, int sortDirection) throws ApiException {
//        Sort.Direction direction;
        String sortProp;
//        if (sortDirection == 0) {
//            direction = Sort.Direction.ASC;
//        } else {
//            direction = Sort.Direction.DESC;
//        }

        switch (sortColumn) {
            case 0:
                sortProp = "createTime";
                break;
            case 1:
                sortProp = "activeTime";
                break;
            case 2:
                sortProp = "giftCoin";
                break;
            case 3:
                sortProp = "messageCoin";
                break;
            case 4:
                sortProp = "audioCoin";
                break;
            case 5:
                sortProp = "videoCoin";
                break;
            case 6:
                sortProp = "rechargeCoin";
                break;
            case 7:
                sortProp = "coin";
                break;
            default:
                sortProp = "createTime";
                break;
        }

        if (sortDirection == 0) {
            sortProp += " ASC";
        } else {
            sortProp += " DESC";
        }

//        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize, direction, sortProp);
//        Page<ConsumerDto> consumerDtoPage = userRepository
//                .consumerStatistics(digitId, nick, phone, startTime, endTime, pageable);

        List<ConsumerDto> consumerDtoList = userRepository
                .consumerStatisticsEx2(digitId, nick, phone, startTime, endTime, pageSize, (pageIndex - 1) * pageSize, sortProp);

        CalcConsumeDto calcConsumeDto = userRepository.calcConsume(digitId, nick, phone, startTime, endTime);
        Long totalCount = calcConsumeDto.getCount();
        Long totalPage = (totalCount / pageSize) + 1;

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", totalPage);
        extra.put("totalEle", totalCount);
        extra.put("currPage", pageIndex);
        extra.put("totalUser", calcConsumeDto.getCount());
        extra.put("totalRecharge", calcConsumeDto.getRecharge());

        return Pair.of(consumerDtoList, extra);
    }

    @Override
    public User getUserProfile(String digitId) throws ApiException {
        User user = userService.getUserByDigitId(digitId);
        // 附带用户金币数据
        Asset asset = assetRepository.findByUserId(user.getId());
        user.setCoin(asset.getCoin());
        // 附带收益比例
        user.setVideoProfitRatio(asset.getVideoProfitRatio());
        user.setVoiceProfitRatio(asset.getVoiceProfitRatio());
        user.setMsgProfitRatio(asset.getMsgProfitRatio());
        user.setGiftProfitRatio(asset.getGiftProfitRatio());

        user.setBirthDay(user.getBirth());

        return userService.profile(user);
    }

    @Override
    public User getUserProfileAudit(String digitId) throws ApiException {
        User user = userService.getUserByDigitId(digitId);

        // 判断是否在审核中
        // 如果在审核中，则用审核中的数据覆盖当前的
        Optional<IdentityAudit> userProfileAudit = identityAuditService.getLastProfileAudit(user);
        Optional<IdentityAudit> userPhotosAudit = identityAuditService.getLastPhotosAudit(user);
        Optional<IdentityAudit> userVideoAudit = identityAuditService.getLastVideoAudit(user);
        Optional<IdentityAudit> userGuardPhotosAudit = identityAuditService.getLastGuardPhotosAudit(user);
        Optional<IdentityAudit> userAuditVideoAudit = identityAuditService.getLastAuditVideoAudit(user);

        userProfileAudit.ifPresent(identityAudit -> {
            if (identityAudit.getStatus() == 10) {
                // TODO: 2021/4/2 写个mapper 转换
                if (Objects.nonNull(identityAudit.getHead())) {
                    user.setHead(identityAudit.getHead());
                }
                if (Objects.nonNull(identityAudit.getNick())) {
                    user.setNick(identityAudit.getNick());
                }
                if (Objects.nonNull(identityAudit.getBirth())) {
                    user.setBirth(identityAudit.getBirth());
                }
                if (Objects.nonNull(identityAudit.getCity())) {
                    user.setCity(identityAudit.getCity());
                }
                if (Objects.nonNull(identityAudit.getProfession())) {
                    user.setProfession(identityAudit.getProfession());
                }
                if (Objects.nonNull(identityAudit.getIntro())) {
                    user.setIntro(identityAudit.getIntro());
                }
                if (Objects.nonNull(identityAudit.getLabels())) {
                    String[] labelList = identityAudit.getLabels().split(",");
                    user.setLabels(_innerCreateLabels(labelList));
                }
                if (Objects.nonNull(identityAudit.getCallPrice())) {
                    user.setCallPrice(identityAudit.getCallPrice());
                }
                if (Objects.nonNull(identityAudit.getVideoPrice())) {
                    user.setVideoPrice(identityAudit.getVideoPrice());
                }
                if (Objects.nonNull(identityAudit.getMessagePrice())) {
                    user.setMessagePrice(identityAudit.getMessagePrice());
                }
                if (Objects.nonNull(identityAudit.getVoice())) {
                    user.setVoice(identityAudit.getVoice());
                }
            }
        });

        userPhotosAudit.ifPresent(identityAudit -> {
            if (Objects.nonNull(identityAudit.getPhotos())) {
                user.setPhotos(identityAudit.getPhotos());
            }
        });
//        // 守护版本审核功能替换
//        userPhotosAudit.ifPresent(identityAudit -> {
//            List<UserMaterial> photos = userMaterialService.getAuditPhotos(user);
//            if (photos.size() > 0) {
//                user.setOriginPhotos(photos);
//            }
//        });

        userVideoAudit.ifPresent(identityAudit -> {
            if (Objects.nonNull(identityAudit.getVideo())) {
                user.setVideo(identityAudit.getVideo());
            }
        });

        userGuardPhotosAudit.ifPresent(identityAudit -> {
            if (Objects.nonNull(identityAudit.getGuardPhotos())) {
                user.setGuardPhotos(identityAudit.getGuardPhotos());
            }
        });

        userAuditVideoAudit.ifPresent(identityAudit -> {
            if (Objects.nonNull(identityAudit.getAuditVideo())) {
                user.setAuditVideo(identityAudit.getAuditVideo());
            }
            if (Objects.nonNull(identityAudit.getAuditVideoCode())) {
                user.setVideoAuditCode(identityAudit.getAuditVideoCode());
            }
        });

        // 附带用户金币数据
        Asset asset = assetRepository.findByUserId(user.getId());
        user.setCoin(asset.getCoin());
        // 附带收益比例
        user.setVideoProfitRatio(asset.getVideoProfitRatio());
        user.setVoiceProfitRatio(asset.getVoiceProfitRatio());
        user.setMsgProfitRatio(asset.getMsgProfitRatio());
        user.setGiftProfitRatio(asset.getGiftProfitRatio());

        user.setBirthDay(user.getBirth());

        return userService.profile(user);
    }

    @Override
    public Pair<List<RecommendDto>, Map<String, Object>> getRecommendList(String digitId, String nick, Integer gender,
                                                                          Integer type, int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<RecommendDto> recommendDtoPage =
                recommendRepository.getRecommendList(digitId, nick, gender, type, pageable);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", recommendDtoPage.getTotalPages());
        extra.put("totalEle", recommendDtoPage.getTotalElements());
        extra.put("currPage", pageIndex);

        return Pair.of(recommendDtoPage.getContent(), extra);
    }

    @Override
    public int configRecommend(String digitId, int type, int recommend, Long level,
                               Date startTime, Date endTime, Date startTime2, Date endTime2) throws ApiException {

        if (Objects.nonNull(level) && level < 1) {
            throw new ApiException(-1, "推荐值不能小于1");
        }

        if (Objects.nonNull(level) && level > 999999) {
            throw new ApiException(-1, "推荐值过大，不超过999999");
        }

        User user = userService.getUserByDigitId(digitId);
        Optional<Recommend> optionalRecommend = recommendRepository.findByUserIdAndType(user.getId(), type);
        Recommend recommendDo;
        if (!optionalRecommend.isPresent()) {
            if (recommend == 0) {
                throw new ApiException(-1, "该用户未被推荐，无需取消推荐");
            }
            recommendDo = new Recommend();
            recommendDo.setUserId(user.getId());
            recommendDo.setType(type);
            recommendDo.setRecommend(1);
            recommendDo.setLevel(level);

            // 推荐和通话主播设定排班时间
            if (type == 0 || type == 6 || type == 7 || type == 4) {
                // 通话主播需要设定排班时间段
                recommendDo.setStartTime(startTime);
                recommendDo.setEndTime(endTime);
                recommendDo.setStartTime2(startTime2);
                recommendDo.setEndTime2(endTime2);
            }

        } else {
            recommendDo = optionalRecommend.get();
            recommendDo.setRecommend(recommend);
            if (Objects.nonNull(level)) {
                recommendDo.setLevel(level);
            }

            if (recommend == 1 && (type == 0 || type == 6 || type == 7 || type == 4)) {
                // 推荐和通话主播设定排班时间
                recommendDo.setStartTime(startTime);
                recommendDo.setEndTime(endTime);
                recommendDo.setStartTime2(startTime2);
                recommendDo.setEndTime2(endTime2);
            }
        }

        if (recommend == 1) {
            // 设置推荐
            List<Recommend> recommendList = recommendRepository
                    .findByTypeAndLevelGreaterThanEqualAndRecommend(type, level, 1);

            // 将之前等于小于level的都加1
            List<Recommend> adjustList = recommendList
                    .stream()
                    .filter(r -> !r.getId().equals(recommendDo.getId()))
                    .peek(r -> r.setLevel(r.getLevel() + 1))
                    .collect(Collectors.toList());

            adjustList.add(recommendDo);

            recommendRepository.saveAll(adjustList);
        } else {
            recommendRepository.save(recommendDo);
        }

        return 1;
    }

    @Override
    public Pair<List<IdentityAudit>, Map<String, Object>> getAnchorAuditList(String digitId, String nick, Integer gender, Integer status,
                                                  Date startTime, Date endTime,
                                                  int pageIndex, int pageSize) throws ApiException {

        // status 0：审核中 1： 审核未通过 2： 全部
        List<Integer> statusList = new ArrayList<>();
        if (status.equals(0)) {
            statusList.add(10);
        } else if (status.equals(1)) {
            statusList.add(20);
        } else {
            statusList.add(10);
            statusList.add(20);
        }

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<IdentityAudit> identityAuditPage = identityAuditRepository
                .getIdentityAuditList(digitId, nick, gender, statusList, startTime, endTime, pageable);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", identityAuditPage.getTotalPages());
        extra.put("totalEle", identityAuditPage.getTotalElements());
        extra.put("currPage", pageIndex);

        return Pair.of(identityAuditPage.getContent(), extra);
    }

    @Override
    public List<IdentityAudit> getAnchorAudit(String digitId) throws ApiException {
        User user = userService.getUserByDigitId(digitId);
        return identityAuditRepository.getLatestWithAllType(user.getId());
    }

    @Transactional
//    @ReTry(value = {PessimisticLockingFailureException.class})
//    @Retryable(value = {PessimisticLockingFailureException.class})
    @Override
    public int anchorAudit(String serialNumber, int operate, String message) throws ApiException {
        IdentityAudit identityAudit = identityAuditRepository.findBySerialNumber(serialNumber);

        if (identityAudit.getStatus() != 10) {
            throw new ApiException(-1, "该条审核已完成！");
        }

        User user = userService.getUserById(identityAudit.getUserId());
        if (operate == 1) {
            // 审核通过
            identityAudit.setStatus(30);

            // 清空审核准备资料
            int type = identityAudit.getType();
            Optional<IdentityAudit> identityAuditOptional = identityAuditService
                    .getLastAuditPrepare(identityAudit.getUserId(), type + 10);
            if (identityAuditOptional.isPresent()) {
                IdentityAudit identityAuditPrepare = identityAuditOptional.get();
                identityAuditPrepare.setStatus(30);
                identityAuditRepository.save(identityAuditPrepare);
            }

//            if (type == 2) {
//                // 守护版本， 处理照片逻辑，删除审核准备的照片，把审核中照片转化成正常照片
//                List<UserMaterial> auditPreparePhotos = userMaterialService.getAuditPreparePhotos(user);
//                userMaterialService.deleteList(auditPreparePhotos);
//
//                List<UserMaterial> auditPhotos = userMaterialService.getAuditPhotos(user);
//                auditPhotos.forEach(userMaterial -> {
//                    userMaterial.setType(0);
//                });
//                userMaterialService.updateList(auditPhotos);
//            }
            if (type == 6) {
                user.setVideoAudit(1);
                userRepository.save(user);
            }

            switch (type) {
                case 1:
                    imService.sendToUser(null, user, "您提交的个人资料已审核通过");
                    break;
                case 2:
                    imService.sendToUser(null, user, "您提交的照片已审核通过");
                    break;
                case 3:
                    imService.sendToUser(null, user, "您提交的视频已审核通过");
                    break;
                case 4:
                    imService.sendToUser(null, user, "您提交的守护照片已审核通过");
                    break;
                case 6:
                    imService.sendToUser(null, user, "您提交的认证视频已审核通过");
                    break;
            }

        } else {
            // 审核不通过
            identityAudit.setStatus(20);

            String typeMsg;
            switch (identityAudit.getType()) {
                case 1:
                    typeMsg = "个人资料";
                    break;
                case 2:
                    typeMsg = "照片";
                    break;
                case 3:
                    typeMsg = "视频";
                    break;
                case 4:
                    typeMsg = "守护照片";
                    break;
                case 6:
                    typeMsg = "认证视频";
                    break;
                default:
                    typeMsg = "资料";
            }
            String remarkMessage = StringUtils.isEmpty(message) ? "未知原因" : message;
            String formatMsg = String.format("您提交的%s审核因%s未通过，请重新提交", typeMsg, remarkMessage);
            imService.sendToUser(null, user, formatMsg);
        }

        if (Objects.nonNull(message)) {
            identityAudit.setMessage(message);
        }

        identityAuditRepository.save(identityAudit);

        if (operate == 1) {
            // 已完成认证, 更新此次认证的数据
            if (Objects.nonNull(identityAudit.getHead())) {
                user.setHead(identityAudit.getHead());
            }

            if (Objects.nonNull(identityAudit.getNick())) {
                user.setNick(identityAudit.getNick());
            }

            if (Objects.nonNull(identityAudit.getBirth())) {
                user.setBirth(identityAudit.getBirth());
            }

            if (Objects.nonNull(identityAudit.getCity())) {
                user.setCity(identityAudit.getCity());
            }

            if (Objects.nonNull(identityAudit.getProfession())) {
                user.setProfession(identityAudit.getProfession());
            }

            if (Objects.nonNull(identityAudit.getIntro())) {
                user.setIntro(identityAudit.getIntro());
            }

            if (Objects.nonNull(identityAudit.getLabels())) {
                String[] labelList = identityAudit.getLabels().split(",");
                user.setLabels(_innerCreateLabels(labelList));
            }

            if (Objects.nonNull(identityAudit.getCallPrice())) {
                user.setCallPrice(identityAudit.getCallPrice());
            }

            if (Objects.nonNull(identityAudit.getVideoPrice())) {
                user.setVideoPrice(identityAudit.getVideoPrice());
            }

            if (Objects.nonNull(identityAudit.getMessagePrice())) {
                user.setMessagePrice(identityAudit.getMessagePrice());
            }

            if (Objects.nonNull(identityAudit.getPhotos())) {
                user.setPhotos(identityAudit.getPhotos());
            }

            if (Objects.nonNull(identityAudit.getVideo())) {
                user.setVideo(identityAudit.getVideo());
            }

            if (Objects.nonNull(identityAudit.getVoice())) {
                user.setVoice(identityAudit.getVoice());
            }

            if (Objects.nonNull(identityAudit.getGuardPhotos())) {
                user.setGuardPhotos(identityAudit.getGuardPhotos());
            }

            if (Objects.nonNull(identityAudit.getAuditVideo())) {
                user.setAuditVideo(identityAudit.getAuditVideo());
            }

            if (Objects.nonNull(identityAudit.getAuditVideoCode())) {
                user.setVideoAuditCode(identityAudit.getAuditVideoCode());
            }

            if (user.getIdentity() == 0) {
                // 判断是否不同的审核都已满足,如果都通过，则完成了认证
                boolean allPass = true;
                for (int type = 1; type <= 3; type++) {
                    if (type == identityAudit.getType()) {
                        continue;
                    }

                    Optional<IdentityAudit> identityAuditOther = identityAuditRepository
                            .findTopByUserIdAndTypeOrderByCreateTimeDesc(identityAudit.getUserId(), type);

                    if (!identityAuditOther.isPresent() || identityAuditOther.get().getStatus() != 30) {
                        allPass = false;
                    }
                }

                if (allPass) {
                    // 都通过认证，则认为身份认证通过, 更新用户信息里的身份认证字段
                    user.setIdentity(1);
                    userRepository.save(user);

                    // 加入到私聊推荐池
                    configRecommend(user.getDigitId(), 3, 1, 1L,
                            null, null, null, null);
                }
            }

            asyncService.setProfileAsync(user, 0L);
        }

        return 1;
    }

    @Override
    public Pair<List<CalcUserDto>, Map<String, Object>> getCalcUser(Date startTime, Date endTime, int pageIndex, int pageSize) throws ApiException {
        return null;
    }

    @Override
    public Pair<List<CalcTotalProfitDto>, Map<String, Object>> getCalcTotalProfit(Date startTime, Date endTime, int pageIndex, int pageSize) throws ApiException {
        return null;
    }

    @Deprecated
    @Override
    public int anchorVideoAudit(String digitId, int operate, String message) throws ApiException {
        log.info("[user manager] anchor video audit with digit id: {}, operate: {}, message: {}.",
                digitId, operate, message);

        if (operate == 1) {
            User user = userService.getUserByDigitId(digitId);
            user.setVideoAudit(1);
            userRepository.save(user);
        }

        return 1;
    }

    // 创建标签
    private List<Label> _innerCreateLabels(String[] labels) {
        List<Label> labelList = new ArrayList<>();
        for (String name: labels) {
            Label label = labelRepository.findByName(name);
            if (label != null) {
                labelList.add(label);
            }
        }
        return labelList;
    }
}
