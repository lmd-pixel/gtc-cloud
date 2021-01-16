package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.social.ConcernDto;
import com.fmisser.gtc.base.dto.social.DynamicDto;
import com.fmisser.gtc.base.dto.social.FansDto;
import com.fmisser.gtc.base.dto.social.FollowDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.domain.Follow;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.FollowRepository;
import com.fmisser.gtc.social.repository.UserRepository;
import com.fmisser.gtc.social.service.FollowService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.fmisser.gtc.social.service.impl.UserServiceImpl.changePhotosToList;

@Service
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final OssConfProp ossConfProp;

    public FollowServiceImpl(FollowRepository followRepository,
                             UserRepository userRepository,
                             OssConfProp ossConfProp) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.ossConfProp = ossConfProp;
    }

    @Override
    public ApiResp<List<FollowDto>> getFollowsFrom(Long userId) throws ApiException {
        return ApiResp.succeed(followRepository.getFollowsFrom(userId));
    }

    @Override
    public ApiResp<List<FollowDto>> getFollowsTo(Long userId) throws ApiException {
        return ApiResp.succeed(followRepository.getFollowsTo(userId));
    }

    @Transactional
    @Override
    public ApiResp<FollowDto> follow(User userFrom, User userTo, boolean bFollow) throws ApiException {

        if (userFrom.getId() == userTo.getId()) {
            throw new ApiException(-1, "不能关注自己!");
        }

        Follow follow = followRepository.findByUserIdFromAndUserIdTo(userFrom.getId(), userTo.getId());
        if (follow == null) {
            follow = new Follow();
            follow.setUserIdFrom(userFrom.getId());
            follow.setUserIdTo(userTo.getId());
        }

//        Optional<User> userOptional = userRepository.findById(userIdTo);
//        if (!userOptional.isPresent()) {
//            throw new ApiException(-1, "被关注的用户不存在");
//        }
//        User userTo = userOptional.get();

        if (bFollow) {
            follow.setStatus(1);
            userRepository.addUserFollow(userTo.getId());
        } else {
            follow.setStatus(0);
            userRepository.subUserFollow(userTo.getId());
        }

        follow = followRepository.save(follow);

        // create dto
        FollowDto followDto = new FollowDto();
        followDto.setUserIdFrom(follow.getUserIdFrom());
        followDto.setUserIdTo(follow.getUserIdTo());
        followDto.setCreateTime(follow.getCreateTime());

        return ApiResp.succeed(followDto);
    }

    @Override
    public List<ConcernDto> getConcernList(User user, int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<ConcernDto> concernDtoList = followRepository
                .getConcernList(user.getId(), pageable).getContent();

        return _prepareConcernDtoResponse(concernDtoList);
    }

    @Override
    public List<FansDto> getFansList(User user, int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<FansDto> fansDtoList = followRepository
                .getFansList(user.getId(), pageable).getContent();

        return _prepareFansDtoResponse(fansDtoList);

    }

    private List<ConcernDto> _prepareConcernDtoResponse(List<ConcernDto> concernDtos) {
        for (ConcernDto concernDto:
                concernDtos) {

            // 完善个人信息数据

            if (concernDto.getBirth() != null) {
                // 通过生日计算年龄
                concernDto.setAge(DateUtils.getAgeFromBirth(concernDto.getBirth()));
            }

            if (!StringUtils.isEmpty(concernDto.getHead())) {
                String headUrl = String.format("%s/%s/%s",
                        ossConfProp.getMinioVisitUrl(),
                        ossConfProp.getUserProfileBucket(),
                        concernDto.getHead());
                String headThumbnailUrl = String.format("%s/%s/thumbnail_%s",
                        ossConfProp.getMinioVisitUrl(),
                        ossConfProp.getUserProfileBucket(),
                        concernDto.getHead());
                concernDto.setHeadUrl(headUrl);
                concernDto.setHeadThumbnailUrl(headThumbnailUrl);
            }
        }

        return concernDtos;
    }

    private List<FansDto> _prepareFansDtoResponse(List<FansDto> fansDtos) {
        for (FansDto fansDto:
                fansDtos) {

            // 完善个人信息数据
            if (fansDto.getBirth() != null) {
                // 通过生日计算年龄
                fansDto.setAge(DateUtils.getAgeFromBirth(fansDto.getBirth()));
            }

            if (!StringUtils.isEmpty(fansDto.getHead())) {
                String headUrl = String.format("%s/%s/%s",
                        ossConfProp.getMinioVisitUrl(),
                        ossConfProp.getUserProfileBucket(),
                        fansDto.getHead());
                String headThumbnailUrl = String.format("%s/%s/thumbnail_%s",
                        ossConfProp.getMinioVisitUrl(),
                        ossConfProp.getUserProfileBucket(),
                        fansDto.getHead());
                fansDto.setHeadUrl(headUrl);
                fansDto.setHeadThumbnailUrl(headThumbnailUrl);
            }
        }

        return fansDtos;
    }

}
