package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.*;
import com.fmisser.gtc.base.dto.social.calc.CalcTotalProfitDto;
import com.fmisser.gtc.base.dto.social.calc.CalcUserDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.IdentityAudit;
import com.fmisser.gtc.social.domain.Recommend;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.*;
import com.fmisser.gtc.social.service.UserManagerService;
import com.fmisser.gtc.social.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserManagerServiceImpl implements UserManagerService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RecommendRepository recommendRepository;
    private final IdentityAuditRepository identityAuditRepository;
    private final AssetRepository assetRepository;

    public UserManagerServiceImpl(UserService userService,
                                  UserRepository userRepository,
                                  RecommendRepository recommendRepository,
                                  IdentityAuditRepository identityAuditRepository,
                                  AssetRepository assetRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.recommendRepository = recommendRepository;
        this.identityAuditRepository = identityAuditRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    public List<AnchorDto> getAnchorList(String digitId, String nick, String phone, Integer gender,
                                         Date startTime, Date endTime,
                                         int pageIndex, int pageSize,
                                         int sortColumn, int sortDirection) throws ApiException {
        Sort.Direction direction;
        String sortProp;
        if (sortDirection == 0) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

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

        Pageable pageable = PageRequest.of(pageIndex, pageSize, direction, sortProp);
        return userRepository
                .anchorStatistics(digitId, nick, phone, gender, startTime, endTime, pageable)
                .getContent();
    }

    @Override
    public List<ConsumerDto> getConsumerList(String digitId, String nick, String phone,
                                             Date startTime, Date endTime,
                                             int pageIndex, int pageSize,
                                             int sortColumn, int sortDirection) throws ApiException {
        Sort.Direction direction;
        String sortProp;
        if (sortDirection == 0) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

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

        Pageable pageable = PageRequest.of(pageIndex, pageSize, direction, sortProp);
        return userRepository
                .consumerStatistics(digitId, nick, phone, startTime, endTime, pageable)
                .getContent();
    }

    @Override
    public User getUserProfile(String digitId) throws ApiException {
        User user = userService.getUserByDigitId(digitId);
        // 附带用户金币数据
        Asset asset = assetRepository.findByUserId(user.getId());
        user.setCoin(asset.getCoin());
        return userService.profile(user);
    }

    @Override
    public List<RecommendDto> getRecommendList(String digitId, String nick, Integer type,
                                               int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        return recommendRepository.getRecommendList(digitId, nick, type, pageable).getContent();
    }

    @Override
    public int configRecommend(String digitId, int type, int recommend, Long level) throws ApiException {
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
        } else {
            recommendDo = optionalRecommend.get();
            recommendDo.setRecommend(recommend);
            if (Objects.nonNull(level)) {
                recommendDo.setLevel(level);
            }
        }
        recommendRepository.save(recommendDo);
        return 1;
    }

    @Override
    public List<IdentityAudit> getAnchorAuditList(String digitId, String nick, Integer gender, Integer status,
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
        return identityAuditRepository.getIdentityAuditList(digitId, nick, gender, statusList, startTime, endTime, pageable)
                .getContent();
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

        if (operate == 1) {
            // 审核通过
            identityAudit.setStatus(30);
        } else {
            // 审核不通过
            identityAudit.setStatus(20);
        }

        if (Objects.nonNull(message)) {
            identityAudit.setMessage(message);
        }

        identityAuditRepository.save(identityAudit);

        if (operate == 1) {
            User user = userRepository.findById(identityAudit.getUserId()).get();

            if (user.getIdentity() == 1) {
                // TODO: 2020/11/30  已完成身份认证, 更新此次认证的数据
            } else {
                // 判断是否不同的审核都已满足,如果都通过，则完成了认证
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
                    // TODO: 2020/12/5 主播认证通过
                    user.setIdentity(1);
                    userRepository.save(user);
                }
            }
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
}
