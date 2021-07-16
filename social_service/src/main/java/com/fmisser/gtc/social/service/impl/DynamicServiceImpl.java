package com.fmisser.gtc.social.service.impl;

import com.fmisser.fpp.oss.cos.service.CosService;
import com.fmisser.gtc.base.aop.ReTry;
import com.fmisser.gtc.base.dto.social.DynamicCommentDto;
import com.fmisser.gtc.base.dto.social.DynamicDto;
import com.fmisser.gtc.base.dto.social.GuardDto;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.base.utils.CryptoUtils;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.domain.Dynamic;
import com.fmisser.gtc.social.domain.DynamicComment;
import com.fmisser.gtc.social.domain.DynamicHeart;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.repository.DynamicCommentRepository;
import com.fmisser.gtc.social.repository.DynamicHeartRepository;
import com.fmisser.gtc.social.repository.DynamicRepository;
import com.fmisser.gtc.social.service.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.fmisser.gtc.social.service.impl.UserServiceImpl.*;

@Slf4j
@Service
@AllArgsConstructor
public class DynamicServiceImpl implements DynamicService {
    private final DynamicRepository dynamicRepository;
    private final DynamicHeartRepository dynamicHeartRepository;
    private final DynamicCommentRepository dynamicCommentRepository;
    private final OssConfProp ossConfProp;
    private final ImCallbackService imCallbackService;
    private final SysConfigService sysConfigService;
//    private final OssService ossService;
//    private final MinioService minioService;
    private final CosService cosService;
    private final GuardService guardService;
    private final ImService imService;
    private final UserService userService;
    private final CommonService commonService;
    private final AsyncService asyncService;

    @Override
    @SneakyThrows
    public DynamicDto create(User user, int type, String content, String city,
                             BigDecimal longitude, BigDecimal latitude,
                          Map<String, MultipartFile> multipartFileMap) throws ApiException {

//        int ret = imCallbackService.textModeration(user.getDigitId(), content, "biz_dymic", 1);
//        if (ret == 0) {
//            throw new ApiException(-1, "发现违规内容，发表失败");
//        }

        Date todayStart = DateUtils.getDayStart(new Date());
        Date todayEnd = DateUtils.getDayEnd(new Date());
        long count = dynamicRepository.countTodayDynamic(user.getId(), todayStart, todayEnd);
        if (count >= sysConfigService.getDynamicDailyCountLimit()) {
            throw new ApiException(-1, "今日发动态次数已达上限");
        }

        Dynamic dynamic = new Dynamic();
        dynamic.setUserId(user.getId());
        dynamic.setType(type);
        dynamic.setContent(content);
        if (type < 20) {
            // 非守护动态 文本先审核，审核不通过 直接人工审核
            int ret = imCallbackService.textModeration(user.getDigitId(), content, "biz_dymic", 1);
            if (ret == 0) {
                dynamic.setStatus(1);
            } else {
                dynamic.setStatus(10);
            }
        } else {
            // 守护动态需要人工审核
            dynamic.setStatus(1);
        }

        if (Objects.nonNull(city)) {
            dynamic.setCity(city);
        } else {
            // 如果没有传city，则使用信息所在城市
            dynamic.setCity(user.getCity());
        }

        if (Objects.nonNull(longitude) && Objects.nonNull(latitude)) {
            dynamic.setLongitude(longitude);
            dynamic.setLatitude(latitude);
        }

        dynamic = dynamicRepository.save(dynamic);

        List<String> photoList = new ArrayList<>();

        for (MultipartFile file: multipartFileMap.values()) {
            if (file.isEmpty()) {
                continue;
            }

            String randomUUID = UUID.randomUUID().toString();

            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            String suffixName = filename.substring(filename.lastIndexOf("."));

            if (type == 1 || type == 11 || type == 21) {
                // 图片
                if (!isPictureSupported(suffixName)) {
                    throw new ApiException(-1, "图片格式不支持!");
                }

                String objectName = String.format("%s%s_%s_%s%s",
                        ossConfProp.getCosUserDynamicPicturePrefix(),
                        CryptoUtils.base64AesSecret(user.getUsername(), ossConfProp.getObjectAesKey()),
                        new Date().getTime(),
                        randomUUID,
                        suffixName);

                Map<String, String> headers = new HashMap<>(1);
                headers.put("x-cos-meta-dynamic-id", String.valueOf(dynamic.getId()));
                cosService.putObject(ossConfProp.getUserDynamicCosBucket(),
                        ossConfProp.getCosUserDynamicRootPath() + objectName,
                        headers,
                        inputStream, file.getSize(), "image/png");

                if (!StringUtils.isEmpty(objectName)) {
                    photoList.add(objectName);
                }

            } else if (type == 2 || type == 12 || type == 22) {
                // 视频
                if (!isVideoSupported(suffixName)) {
                    throw new ApiException(-1, "视频格式不支持!");
                }

                // 视频暂不提供压缩
                String objectName = String.format("%s%s_%s_%s%s",
                        ossConfProp.getCosUserDynamicVideoPrefix(),
                        CryptoUtils.base64AesSecret(user.getUsername(), ossConfProp.getObjectAesKey()),
                        new Date().getTime(),
                        randomUUID,
                        suffixName);

                Map<String, String> headers = new HashMap<>(1);
                headers.put("x-cos-meta-dynamic-id", String.valueOf(dynamic.getId()));
                cosService.putObject(ossConfProp.getUserDynamicCosBucket(),
                        ossConfProp.getCosUserDynamicRootPath() + objectName,
                        headers,
                        inputStream, file.getSize(), "video/mp4");

                dynamic.setVideo(objectName);

                // 只处理第一个能处理的视频
                break;
            }
        }

        if (type == 1 || type == 11 || type == 21) {
            // 图片
            if (photoList.size() == 0 ) {
                throw new ApiException(-1, "上传信息出错,请稍后重试");
            }

            dynamic.setPictures(photoList.toString());
        } else if (type == 2 || type == 12 || type == 22) {
            if (StringUtils.isEmpty(dynamic.getVideo())) {
                throw new ApiException(-1, "上传信息不正确");
            }
        }

        dynamic = dynamicRepository.save(dynamic);

//        final Long dynamicId = dynamic.getId();
//        // 非守护动态，图片审查
//        if (type == 1 || type == 11) {
//            CompletableFuture<Integer> future = asyncService.dynamicPicAuditAsync(dynamicId, photoList);
//            future.thenAccept(integer -> {
//                if (integer == 0) {
//                    log.info("dynamic: {} was blocked.", dynamicId);
//                    // 审核不通过，直接走人工审核
//                    dynamicRepository.findById(dynamicId)
//                            .ifPresent(d -> {
//                                if (d.getStatus() != 1) {
//                                    d.setStatus(1);
//                                    dynamicRepository.save(d);
//                                }
//                            });
//                } else {
//                    log.info("dynamic: {} was pass.", dynamicId);
//                }
//            });
//        }
//
//        // 非守护动态，视频审查
//        if (type == 2 || type == 12) {
//            CompletableFuture<Integer> future = asyncService.dynamicVideoAuditAsync(dynamicId, dynamic.getVideo());
//            future.thenAcceptAsync(integer -> {
//                if (integer == 0) {
//                    log.info("dynamic: {} was blocked.", dynamicId);
//                    // 审核不通过，直接走人工审核
//                    dynamicRepository.findById(dynamicId)
//                            .ifPresent(d -> {
//                                if (d.getStatus() != 1) {
//                                    d.setStatus(1);
//                                    dynamicRepository.save(d);
//                                }
//                            });
//                } else {
//                    log.info("dynamic: {} was pass.", dynamicId);
//                }
//            });
//        }

        return _prepareDynamicResponse(dynamic, user);
    }

    @Override
    public List<DynamicDto> getUserDynamicList(User user, User selfUser, int pageIndex, int pageSize, String version) throws ApiException {
        // 如果不提供自己的 user id 则默认设置为0
        Long selfUserId = 0L;
        if (selfUser != null) {
            selfUserId = selfUser.getId();
        }

        // 审核控制
        Date dateLimit = sysConfigService.getAppAuditDynamicDateLimit(version);

        List<Integer> status;
        if (selfUserId.equals(user.getId())) {
            // 自己可以看见审核中的动态
            status = Arrays.asList(1, 10);
        } else {
            status = Collections.singletonList(10);
        }

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<DynamicDto> dynamicDtos = dynamicRepository
                .getUserDynamicList(user.getId(), selfUserId, status, dateLimit, pageable);

        List<GuardDto> guardDtoList;
        if (Objects.nonNull(selfUser)) {
            guardDtoList = guardService.getUserGuardList(selfUser);
        } else {
            guardDtoList = new ArrayList<>();
        }

        return _prepareDynamicDtoResponse(dynamicDtos.getContent(), guardDtoList, false);
    }

    @Override
//    @ReTry(value = {PessimisticLockingFailureException.class})
    public int dynamicHeart(Long dynamicId, User selfUser, int isCancel) throws ApiException {
        Optional<Dynamic> optionalDynamic = dynamicRepository.findById(dynamicId);
        if (!optionalDynamic.isPresent()) {
            throw new ApiException(-1, "动态数据不存在或已删除！");
        }
        Dynamic dynamic = optionalDynamic.get();

        DynamicHeart dynamicHeart;
        Optional<DynamicHeart> optionalDynamicHeart =
                dynamicHeartRepository.findByDynamicIdAndUserId(dynamicId, selfUser.getId());

        if (optionalDynamicHeart.isPresent()) {
            dynamicHeart = optionalDynamicHeart.get();

            if (dynamicHeart.getIsCancel() == isCancel) {
                throw new ApiException(-1, "已经点赞或取消，请勿重复操作！");
            }

            dynamicHeart.setIsCancel(isCancel);
        } else {

            if (isCancel == 1) {
                throw new ApiException(-1, "没有点赞记录无法取消！");
            }

            dynamicHeart = new DynamicHeart();
            dynamicHeart.setUserId(selfUser.getId());
            dynamicHeart.setDynamicId(dynamic.getId());
        }

        dynamicHeartRepository.save(dynamicHeart);

        return 1;
    }

    @Override
//    @ReTry(value = {PessimisticLockingFailureException.class})
    public int newDynamicComment(Long dynamicId, Long commentIdTo, User selfUser, User toUser, String content) throws ApiException {
        Optional<Dynamic> optionalDynamic = dynamicRepository.findById(dynamicId);
        if (!optionalDynamic.isPresent()) {
            throw new ApiException(-1, "动态数据不存在或已删除！");
        }

//        int ret = imCallbackService.textModeration(selfUser.getDigitId(), content, "", 0);
//        if (ret == 0) {
//            throw new ApiException(-1, "发现违规内容，发表失败");
//        }

        Dynamic dynamic = optionalDynamic.get();

        DynamicComment dynamicComment = new DynamicComment();
        dynamicComment.setDynamicId(dynamic.getId());
        dynamicComment.setUserIdFrom(selfUser.getId());
        // 判断是不是回复某个人
        if (Objects.nonNull(commentIdTo) && Objects.nonNull(toUser)) {
            dynamicComment.setCommentIdTo(commentIdTo);
            dynamicComment.setUserIdTo(toUser.getId());
        }
        dynamicComment.setContent(content);

        int ret = imCallbackService.textModeration(selfUser.getDigitId(), content, "biz_dymic", 1);
        if (ret == 0) {
            dynamicComment.setIsDelete(1);
        }

        dynamicCommentRepository.save(dynamicComment);

        return 1;
    }

    @Override
    @ReTry(value = {PessimisticLockingFailureException.class})
    public int delDynamicComment(Long commentId, User selfUser) throws ApiException {
        Optional<DynamicComment> optionalDynamicComment = dynamicCommentRepository.findById(commentId);
        if (!optionalDynamicComment.isPresent()) {
            throw new ApiException(-1, "评论数据不存在或已删除！");
        }

        DynamicComment dynamicComment = optionalDynamicComment.get();
        if (!dynamicComment.getUserIdFrom().equals(selfUser.getId()) ) {
            throw new ApiException(-1, "无权限删除评论！");
        }

        if (dynamicComment.getIsDelete() == 1) {
            throw new ApiException(-1, "请勿重复删除！");
        }

        dynamicComment.setIsDelete(1);
        dynamicCommentRepository.save(dynamicComment);

        return 1;
    }

    @Override
    public List<DynamicCommentDto> getDynamicCommentList(Long dynamicId, User selfUser,
                                                         int pageIndex, int pageSize) throws ApiException {
        // 如果不提供自己的 user id 则默认设置为0
        Long selfUserId = 0L;
        if (selfUser != null) {
            selfUserId = selfUser.getId();
        }

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<DynamicCommentDto> dynamicCommentDtos = dynamicCommentRepository
                .getCommentList(dynamicId, pageable)
                .getContent();

        return _prepareDynamicCommentDtoResponse(dynamicCommentDtos, selfUserId);
    }

    @Override
    public List<DynamicDto> getLatestDynamicList(User selfUser, int pageIndex, int pageSize, String version) throws ApiException {
        // 如果不提供自己的 user id 则默认设置为0
        Long selfUserId = 0L;
        if (selfUser != null) {
            selfUserId = selfUser.getId();
        }

        // 审核控制
        Date dateLimit = sysConfigService.getAppAuditDynamicDateLimit(version);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        List<DynamicDto> dynamicDtos = dynamicRepository
                .getLatestDynamicList(selfUserId, dateLimit, pageable).getContent();

        List<GuardDto> guardDtoList;
        if (Objects.nonNull(selfUser)) {
            guardDtoList = guardService.getUserGuardList(selfUser);
        } else {
            guardDtoList = new ArrayList<>();
        }
        return _prepareDynamicDtoResponse(dynamicDtos, guardDtoList, false);
    }

    @Override
    public List<DynamicDto> getFollowLatestDynamicList(User selfUser, int pageIndex, int pageSize, String version) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        // 审核控制
        Date dateLimit = sysConfigService.getAppAuditDynamicDateLimit(version);

        List<DynamicDto> dynamicDtos = dynamicRepository
                .getDynamicListByFollow(selfUser.getId(), dateLimit, pageable).getContent();

        List<GuardDto> guardDtoList = guardService.getUserGuardList(selfUser);
        return _prepareDynamicDtoResponse(dynamicDtos, guardDtoList, false);
    }

    @Override
    public int delete(User user, Long dynamicId) throws ApiException {
        Optional<Dynamic> dynamicOptional = dynamicRepository.findById(dynamicId);
        if (!dynamicOptional.isPresent()) {
            throw new ApiException(-1, "动态不存在，无法删除!");
        }

        Dynamic dynamic = dynamicOptional.get();
        if (!dynamic.getUserId().equals(user.getId())) {
            throw new ApiException(-1, "您没有权限这样操作!");
        }

        if (dynamic.getIsDelete() == 1) {
            throw new ApiException(-1, "动态不存在,无法删除!");
        }

        dynamic.setIsDelete(1);
        dynamicRepository.save(dynamic);

        return 1;
    }

    @Override
    public Pair<List<DynamicDto>, Map<String, Object>> managerListDynamic(String digitId, String nick, String content,
                                   Date startTime, Date endTime,
                                   int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<DynamicDto> dynamicDtoPage =
                dynamicRepository.getManagerDynamicList(digitId, nick, startTime, endTime, pageable);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", dynamicDtoPage.getTotalPages());
        extra.put("totalEle", dynamicDtoPage.getTotalElements());
        extra.put("currPage", pageIndex);

        return Pair.of(_prepareDynamicDtoResponse(dynamicDtoPage.getContent(), Collections.emptyList(), true), extra);
    }

    @Override
    public Pair<List<DynamicDto>, Map<String, Object>> managerGuardListDynamic(String digitId, String nick, String content,
                                                                               Date startTime, Date endTime,
                                                                               int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        Page<DynamicDto> dynamicDtoPage =
                dynamicRepository.getManagerGuardDynamicList(digitId, nick, startTime, endTime, pageable);

        Map<String, Object> extra = new HashMap<>();
        extra.put("totalPage", dynamicDtoPage.getTotalPages());
        extra.put("totalEle", dynamicDtoPage.getTotalElements());
        extra.put("currPage", pageIndex);

        return Pair.of(_prepareDynamicDtoResponse(dynamicDtoPage.getContent(), Collections.emptyList(), true), extra);
    }

    @Override
    public int deleteDynamic(Long dynamicId) throws ApiException {
        Optional<Dynamic> dynamicOptional = dynamicRepository.findById(dynamicId);
        if (!dynamicOptional.isPresent()) {
            throw new ApiException(-1, "动态不存在，无法删除!");
        }

        Dynamic dynamic = dynamicOptional.get();

        if (dynamic.getIsDelete() == 1) {
            throw new ApiException(-1, "动态不存在,无法删除!");
        }

        dynamic.setIsDelete(1);
        dynamicRepository.save(dynamic);

        return 1;
    }

    @Override
    public int auditDynamic(Long dynamicId, int pass, String message) throws ApiException {
        Optional<Dynamic> dynamicOptional = dynamicRepository.findById(dynamicId);
        if (!dynamicOptional.isPresent()) {
            throw new ApiException(-1, "动态不存在，无法操作!");
        }

        Dynamic dynamic = dynamicOptional.get();
        if (!StringUtils.isEmpty(message)) {
            dynamic.setMessage(message);
        }

        User user = userService.getUserById(dynamic.getUserId());
        if (pass == 1) {
            dynamic.setStatus(10);
            if (dynamic.getType() < 20) {
                imService.sendToUser(null, user, "您发表的动态已审核通过");
            } else {
                imService.sendToUser(null, user, "您发表的守护动态已审核通过");
            }

        } else {
            dynamic.setStatus(20);

            if (dynamic.getType() < 20) {
                if (StringUtils.isEmpty(message)) {
                    imService.sendToUser(null, user, "您发表的动态未审核通过");
                } else {
                    String formatString = String.format("您发表的动态因%s未通过，请重新发表", message);
                    imService.sendToUser(null, user, formatString);
                }
            } else {
                if (StringUtils.isEmpty(message)) {
                    imService.sendToUser(null, user, "您发表的守护动态未审核通过");
                } else {
                    String formatString = String.format("您发表的守护动态因%s未通过，请重新发表", message);
                    imService.sendToUser(null, user, formatString);
                }
            }
        }

        dynamicRepository.save(dynamic);

        return 1;
    }

    @Override
    public int changeDynamic(Long dynamicId, int isGuard) throws ApiException {
        Optional<Dynamic> dynamicOptional = dynamicRepository.findById(dynamicId);
        if (!dynamicOptional.isPresent()) {
            throw new ApiException(-1, "动态不存在，无法操作!");
        }

        Dynamic dynamic = dynamicOptional.get();

        int type = dynamic.getType();
        if (type == 0 || type == 10 || type == 20) {
            if (isGuard == 0) {
                dynamic.setType(10);
            } else {
                dynamic.setType(20);
            }
        }
        if (type == 1 || type == 11 || type == 21) {
            if (isGuard == 0) {
                dynamic.setType(11);
            } else {
                dynamic.setType(21);
            }
        }
        if (type == 2 || type == 12 || type == 22) {
            if (isGuard == 0) {
                dynamic.setType(12);
            } else {
                dynamic.setType(22);
            }
        }
        dynamicRepository.save(dynamic);

        return 1;
    }

    @Override
    public List<DynamicDto> compat(List<DynamicDto> dynamicDtoList, String version) throws ApiException {
        final String compatVersion = "v212";
        if (version.compareTo(compatVersion) < 0) {
            // 兼容以前的动态
            return dynamicDtoList.stream()
                    .peek(dynamicDto -> {
                        dynamicDto.setLatitude(null);
                        dynamicDto.setLongitude(null);
                    }).collect(Collectors.toList());
        } else {
            return dynamicDtoList;
        }
    }

    private List<DynamicCommentDto> _prepareDynamicCommentDtoResponse(List<DynamicCommentDto> dynamicCommentDtos, Long selfUserId) {
        for (DynamicCommentDto commentDto :
                dynamicCommentDtos) {

            if (commentDto.getUserIdFrom().equals(selfUserId)) {
                commentDto.setIsSelf(1L);
            } else {
                commentDto.setIsSelf(0L);
            }

            commonService.getUserProfileHeadCompleteUrl(commentDto.getHead())
                    .ifPresent(v -> {
                        commentDto.setHeadUrl(v.getFirst());
                        commentDto.setHeadThumbnailUrl(v.getSecond());
                    });

        }

        return dynamicCommentDtos;
    }

    private List<DynamicDto> _prepareDynamicDtoResponse(List<DynamicDto> dynamicDtos, List<GuardDto> guardList, boolean isManager) {
        for (DynamicDto dynamicDto:
                dynamicDtos) {

            // 判断是否是守护者
            AtomicBoolean isGuard = new AtomicBoolean(false);
            if (dynamicDto.getType() >= 20) {
                // 守护动态
                guardList.stream()
                        .filter(guardDto -> guardDto.getDigitId().equals(dynamicDto.getDigitId()))
                        .findFirst()
                        .ifPresent(guardDto -> {
                            isGuard.set(true);
                            dynamicDto.setSelfIsGuard(1L);
                        });
            } else {
                // 不是守护动态则当成是守护的可见
                isGuard.set(true);
            }

            // cos 通过设定后缀返回不同作用的图（比如缩略图、模糊效果等）
            final String thumbnail_tail;
            final String origin_tail;
            final String thumbnail_tail_video = "";
            if (isGuard.get() || isManager) {
                thumbnail_tail = "!gsv";
                origin_tail = "!0";
            } else {
                thumbnail_tail = "!ngsv";
                origin_tail = "!n";
            }

            // 提供完整的图片url
            if (!StringUtils.isEmpty(dynamicDto.getPictures())) {
                List<String> pictureNameList = changePhotosToList(dynamicDto.getPictures());
                List<String> pictureUrlList = pictureNameList.stream()
                        .map( name -> String.format("%s/%s%s%s",
                                cosService.getDomainName(ossConfProp.getCosCdn(), ossConfProp.getUserDynamicCosBucket()),
                                ossConfProp.getCosUserDynamicRootPath(), name, origin_tail))
                        .collect(Collectors.toList());

                List<String> pictureThumbnailUrlList = pictureNameList.stream()
                        .map( name -> String.format("%s/%s%s%s",
                                cosService.getDomainName(ossConfProp.getCosCdn(), ossConfProp.getUserDynamicCosBucket()),
                                ossConfProp.getCosUserDynamicRootPath(), name, thumbnail_tail))
                        .collect(Collectors.toList());

                dynamicDto.setPictureUrlList(pictureUrlList);
                dynamicDto.setPictureThumbnailUrlList(pictureThumbnailUrlList);
            }

            // 提供完整的视频链接
            if (!StringUtils.isEmpty(dynamicDto.getVideo())) {
                String pureName = dynamicDto.getVideo().substring(0, dynamicDto.getVideo().lastIndexOf('.'));
                String thumbnailPurlName = StringUtils.replace(pureName, ossConfProp.getCosUserDynamicVideoPrefix(),
                        ossConfProp.getUserDynamicVideoThumbnailPrefix());

                String videoUrl = String.format("%s/%s%s.mp4",
                        cosService.getDomainName(ossConfProp.getCosCdn(), ossConfProp.getUserDynamicCosBucket()),
                        ossConfProp.getCosUserDynamicRootPath(), thumbnailPurlName);
                dynamicDto.setVideoUrl(videoUrl);

                String videoThumbnailUrl =  String.format("%s/%s%s_mute.mp4%s",
                        cosService.getDomainName(ossConfProp.getCosCdn(), ossConfProp.getUserDynamicCosBucket()),
                        ossConfProp.getCosUserDynamicRootPath(), thumbnailPurlName, thumbnail_tail_video);
                dynamicDto.setVideoThumbnailUrl(videoThumbnailUrl);
            }

            // 完善个人信息数据
            if (dynamicDto.getBirth() != null) {
                // 通过生日计算年龄和星座
                dynamicDto.setAge(DateUtils.getAgeFromBirth(dynamicDto.getBirth()));
                dynamicDto.setConstellation(DateUtils.getConstellationFromBirth(dynamicDto.getBirth()));
            }

            commonService.getUserProfileHeadCompleteUrl(dynamicDto.getHead())
                    .ifPresent(v -> {
                        dynamicDto.setHeadUrl(v.getFirst());
                        dynamicDto.setHeadThumbnailUrl(v.getSecond());
                    });
        }

        return dynamicDtos;
    }

    private DynamicDto _prepareDynamicResponse(Dynamic dynamic, User user) {
        DynamicDto dynamicDto = new DynamicDto(
                dynamic.getId(), dynamic.getUserId(), user.getDigitId(),
                dynamic.getContent(), dynamic.getCity(), dynamic.getType(),
                dynamic.getVideo(), dynamic.getPictures(),
                dynamic.getCreateTime(), dynamic.getModifyTime(),
                dynamic.getLongitude(), dynamic.getLatitude(),
                0L, 0L, 0L,
                user.getNick(), user.getBirth(), user.getGender(), user.getHead());

        List<GuardDto> guardDtoList = new ArrayList<>();
        return _prepareDynamicDtoResponse(Collections.singletonList(dynamicDto), guardDtoList, false).get(0);
    }
}
