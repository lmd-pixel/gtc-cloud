package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.dto.im.ImQueryStateDto;
import com.fmisser.gtc.base.dto.im.ImQueryStateResp;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.i18n.SystemTips;
import com.fmisser.gtc.base.prop.ImConfProp;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.base.response.ApiRespHelper;
import com.fmisser.gtc.base.utils.CryptoUtils;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.controller.BlockController;
import com.fmisser.gtc.social.domain.*;
import com.fmisser.gtc.social.feign.ImFeign;
import com.fmisser.gtc.social.mq.GreetDelayedBinding;
import com.fmisser.gtc.social.repository.*;
import com.fmisser.gtc.social.service.*;
import com.fmisser.gtc.social.utils.MinioUtils;
import io.minio.ObjectWriteResponse;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final MinioUtils minioUtils;

    private final LabelRepository labelRepository;

    private final OssConfProp ossConfProp;

    private final IdentityAuditService identityAuditService;

    private final AssetRepository assetRepository;

    private final BlockRepository blockRepository;

    private final FollowRepository followRepository;

    private final InviteRepository inviteRepository;

    private final CouponService couponService;

    private final SystemTips systemTips;

    private final GreetDelayedBinding greetDelayedBinding;

    private final SysConfigService sysConfigService;

    private final IdentityAuditRepository identityAuditRepository;

//    private final ImFeign imFeign;
//
//    private final ImConfProp imConfProp;

    private final ImService imService;

    public UserServiceImpl(UserRepository userRepository,
                           MinioUtils minioUtils,
                           LabelRepository labelRepository,
                           OssConfProp ossConfProp,
                           IdentityAuditService identityAuditService,
                           AssetRepository assetRepository,
                           BlockRepository blockRepository,
                           FollowRepository followRepository,
                           InviteRepository inviteRepository,
                           CouponService couponService,
                           SystemTips systemTips,
                           GreetDelayedBinding greetDelayedBinding,
                           SysConfigService sysConfigService,
//                           ImFeign imFeign,
//                           ImConfProp imConfProp
                           ImService imService,
                           IdentityAuditRepository identityAuditRepository
                           ) {
        this.userRepository = userRepository;
        this.minioUtils = minioUtils;
        this.labelRepository = labelRepository;
        this.ossConfProp = ossConfProp;
        this.identityAuditService = identityAuditService;
        this.assetRepository = assetRepository;
        this.blockRepository = blockRepository;
        this.followRepository = followRepository;
        this.inviteRepository = inviteRepository;
        this.couponService = couponService;
        this.systemTips = systemTips;
        this.greetDelayedBinding = greetDelayedBinding;
        this.sysConfigService = sysConfigService;
//        this.imFeign = imFeign;
//        this.imConfProp = imConfProp;
        this.imService = imService;
        this.identityAuditRepository = identityAuditRepository;
    }

    @Transactional
    @Override
    public User create(String phone, int gender, String inviteCode) throws ApiException {
        // check exist
        if (userRepository.existsByUsername(phone)) {
            // 已经存在
            throw new ApiException(-1, "用户名重复");
        }

        // create
        User user = new User();
        user.setUsername(phone);
        user.setPhone(phone);

//        LocalDateTime dateTime = LocalDateTime.now(ZoneId.systemDefault());
//        Date startHour = Date.from(DateUtils.getHourStart(dateTime).atZone(ZoneId.systemDefault()).toInstant());
//        Date endHour = Date.from(DateUtils.getHourEnd(dateTime).atZone(ZoneId.systemDefault()).toInstant());
        Date date = new Date();
        Date startHour = DateUtils.getHourStart(date);
        Date endHour = DateUtils.getHourEnd(date);
        long count = userRepository.countByCreateTimeBetween(startHour, endHour);
        String digitId = calcDigitId(date, count + 1);
//        Date startDay = DateUtils.getDayStart(date);
//        Date endDay = DateUtils.getDayEnd(date);
//        Long count = userRepository.countByCreateTimeBetween(startDay, endDay);
//        String digitId = calcDigitIdV2(date, count + 1);
        user.setDigitId(digitId);
        user.setNick(String.format("会员%s", digitId));

        LocalDateTime defaultBirth = LocalDateTime.of(1990, 1, 1, 0, 0);
        user.setBirth(Date.from(defaultBirth.atZone(ZoneId.systemDefault()).toInstant()));
        user.setGender(gender);

        user.setLabels(new ArrayList<>());
//        user.setVerifyStatus(new VerifyStatus());
        user = userRepository.save(user);

        // 创建资产
        Asset asset = new Asset();
        asset.setUserId(user.getId());
        assetRepository.save(asset);

        // 设置邀请信息
        if (Objects.nonNull(inviteCode)) {
            // 目前邀请码就是用户的数字id
            Optional<User> inviteUser = userRepository.findByDigitId(inviteCode);
            if (!inviteUser.isPresent()) {
                throw new ApiException(-1, "邀请人信息不正确!");
            }

            // 保存邀请人信息
            Invite invite = new Invite();
            invite.setUserId(inviteUser.get().getId());
            invite.setInvitedUserId(user.getId());
            invite.setInviteCode(inviteCode);
            invite.setType(0);
            inviteRepository.save(invite);
        }

        // 注册送 视频卡 和 聊天券
        if (sysConfigService.isRegSendFreeVideo()) {
            couponService.addCommVideoCoupon(user.getId(), 1, 10);
        }
        if (sysConfigService.isRegSendFreeMsg()) {
            couponService.addCommMsgFreeCoupon(user.getId(), 20, 10);
        }

        // 新用户注册欢迎消息
//        imService.sendToUser(null, user, systemTips.assistNewUserMsg(user.getNick()));
        // 放到消息队列去调用，这时候可能用户还没登录到腾讯im，直接发消息会报错，也可以在后台直接注册到腾讯im再调用
        // 发送消息加入队列, 5秒后发送
        String sendMsgPayload = String
                .format("3,%s,%s,%s", "", user.getId(), systemTips.assistNewUserMsg(user.getNick()));
        Message<String> welcomeDelayedMessage = MessageBuilder
                .withPayload(sendMsgPayload).setHeader("x-delay", 5 * 1000).build();
        boolean ret = greetDelayedBinding.greetDelayedOutputChannel().send(welcomeDelayedMessage);
        if (!ret) {
            // TODO: 2021/1/25 处理发送失败
        }

        return _prepareResponse(user);
    }

    @Override
    public User getUserByUsername(String username) throws ApiException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new ApiException(1005, "用户数据不存在");
        }
        return userOptional.get();
    }

    @Override
    public boolean isUserExist(String username) throws ApiException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.isPresent();
    }

    @Override
    public User getUserByDigitId(String digitId) throws ApiException {
        Optional<User> userOptional = userRepository.findByDigitId(digitId);
        if (!userOptional.isPresent()) {
            throw new ApiException(1005, "用户数据不存在");
        }
        return userOptional.get();
    }

    @Override
    public User getUserById(Long id) throws ApiException {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new ApiException(1005, "用户数据不存在");
        }
        return userOptional.get();
    }

    @Override
    public User profile(User user) throws ApiException {
        return _prepareResponse(user);
    }

    @Override
    public User getSelfProfile(User user) throws ApiException {
        // 判断是否在审核中
        // 如果在审核中，则用审核中的数据覆盖当前的
        Optional<IdentityAudit> userProfileAudit = identityAuditService.getLastProfileAudit(user);
        if (!userProfileAudit.isPresent() ||
                (userProfileAudit.isPresent() && userProfileAudit.get().getStatus() != 10)) {
            userProfileAudit = identityAuditService.getLastProfilePrepare(user);
        }

        Optional<IdentityAudit> userPhotosAudit = identityAuditService.getLastPhotosAudit(user);
        if (!userPhotosAudit.isPresent() ||
                (userPhotosAudit.isPresent() && userPhotosAudit.get().getStatus() != 10)) {
            userPhotosAudit = identityAuditService.getLastPhotosPrepare(user);
        }

        Optional<IdentityAudit> userVideoAudit = identityAuditService.getLastVideoAudit(user);
        if (!userVideoAudit.isPresent() ||
                (userVideoAudit.isPresent() && userVideoAudit.get().getStatus() != 10)) {
            userVideoAudit = identityAuditService.getLastVideoPrepare(user);
        }

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

        userVideoAudit.ifPresent(identityAudit -> {
            if (Objects.nonNull(identityAudit.getVideo())) {
                user.setVideo(identityAudit.getVideo());
            }
        });

        return _prepareResponse(user);
    }

    @SneakyThrows
    @Override
    public User updateProfile(User user, Integer updateType,
                              String nick, String birth, String city, String profession,
                              String intro, String labels, String callPrice, String videoPrice, String messagePrice,
                              Integer mode, Integer rest, String restStartDate, String restEndDate,
                              Map<String, MultipartFile> multipartFileMap) throws ApiException {

        // 操作模式， 1: 普通资料更新,后台更新，不走审核 2: 待审核资料更新 3: 提交审核
        int optionType;
        IdentityAudit audit = null;

        if (updateType == 1) {
            // 认证资料的修改

            // 如果已经有资料在审核
            Optional<IdentityAudit> identityAudit = identityAuditService.getLastProfileAudit(user);
            if (identityAudit.isPresent() &&
                    identityAudit.get().getStatus() == 10) {
                // 资料审核中不能修改
                throw new ApiException(-1, "资料在认证审核中，暂时无法修改");
            }

//            if (user.getIdentity() == 1) {
//                // 主播直接提交审核
//                optionType = 3;
//                audit = identityAuditService.createAuditPrepare(user, 1);
//            } else {
//                // 非主播，直接提交待审资料
//                optionType = 2;
//                audit = identityAuditService.createAuditPrepare(user, 11);
//            }

            // 不管主播用户都直接触发审核
            optionType = 3;
            audit = identityAuditService.createAuditPrepare(user, 11);

        } else {
            optionType = 1;
        }

        // 处理文本结构
        if (nick != null && !nick.isEmpty()) {
            user.setNick(nick);
            if (optionType == 2 || optionType == 3) {
                audit.setNick(nick);
            }
        }
        if (birth != null && !birth.isEmpty()) {
            user.setBirth(new Date(Long.parseLong(birth)));
            if (optionType == 2 || optionType == 3) {
                audit.setBirth(new Date(Long.parseLong(birth)));
            }
        }
        if (city != null && !city.isEmpty()) {
            user.setCity(city);
            if (optionType == 2 || optionType == 3) {
                audit.setCity(city);
            }
        }
        if (profession != null && !profession.isEmpty()) {
            user.setProfession(profession);
            if (optionType == 2 || optionType == 3) {
                audit.setProfession(profession);
            }
        }
        if (intro != null && !intro.isEmpty()) {
            user.setIntro(intro);
            if (optionType == 2 || optionType == 3) {
                audit.setIntro(intro);
            }
        }
        if (labels != null && !labels.isEmpty()) {
            String[] labelList = labels.split(",");
            user.setLabels(_innerCreateLabels(labelList));
            if (optionType == 2 || optionType == 3) {
                audit.setLabels(labels);
            }
        }
        if (callPrice != null && !callPrice.isEmpty()) {
            BigDecimal price = BigDecimal.valueOf(Long.parseLong(callPrice));
            user.setCallPrice(price);
            if (optionType == 2 || optionType == 3) {
                audit.setCallPrice(price);
            }
        }
        if (videoPrice != null && !videoPrice.isEmpty()) {
            BigDecimal price = BigDecimal.valueOf(Long.parseLong(videoPrice));
            user.setVideoPrice(price);
            if (optionType == 2 || optionType == 3) {
                audit.setVideoPrice(price);
            }
        }
        if (messagePrice != null && !messagePrice.isEmpty()) {
            BigDecimal price = BigDecimal.valueOf(Long.parseLong(messagePrice));
            user.setMessagePrice(price);
            if (optionType == 2 || optionType == 3) {
                audit.setMessagePrice(price);
            }
        }
        if (Objects.nonNull(mode)) {
            user.setMode(mode);
        }
        if (Objects.nonNull(rest)) {
            user.setRest(rest);
        }
        if (Objects.nonNull(restStartDate)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            user.setRestStartDate(dateFormat.parse(restStartDate));
        }
        if (Objects.nonNull(restEndDate)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            user.setRestEndDate(dateFormat.parse(restEndDate));
        }

        // 处理表单
        for (MultipartFile file: multipartFileMap.values()) {
            if (file.isEmpty()) {
                continue;
            }

            String randomUUID = UUID.randomUUID().toString();

            String name = file.getName();
            if (name.equals("voice")) {
                InputStream inputStream = file.getInputStream();
                String filename = file.getOriginalFilename();
                String suffixName = filename.substring(filename.lastIndexOf("."));
                if (!isAudioSupported(suffixName)) {
                    throw new ApiException(-1, "语音格式不支持!");
                }

                // object name 格式： prefix/username_date_randomUUID.xxx
                String objectName = String.format("%s%s_%s_%s%s",
                        ossConfProp.getUserProfileVoicePrefix(),
                        CryptoUtils.base64AesSecret(user.getUsername(), ossConfProp.getObjectAesKey()),
                        new Date().getTime(),
                        randomUUID,
                        suffixName);
                ObjectWriteResponse response = minioUtils.put(ossConfProp.getUserProfileBucket(), objectName,
                        inputStream, file.getSize(), "audio/mpeg");

                if (response.object().isEmpty()) {
                    throw new ApiException(-1, "存储语音文件失败!");
                }

                user.setVoice(response.object());
                if (optionType == 2 || optionType == 3) {
                    audit.setVoice(response.object());
                }

            } else if (name.equals("head")) {
                InputStream inputStream = file.getInputStream();
                String filename = file.getOriginalFilename();
                String suffixName = filename.substring(filename.lastIndexOf("."));
                if (!isPictureSupported(suffixName)) {
                    throw new ApiException(-1, "图片格式不支持!");
                }

                // 存储原始图片
                String objectName = String.format("%s%s_%s_%s%s",
                        ossConfProp.getUserProfileHeadPrefix(),
                        CryptoUtils.base64AesSecret(user.getUsername(), ossConfProp.getObjectAesKey()),
                        new Date().getTime(),
                        randomUUID,
                        suffixName);

                ObjectWriteResponse response = minioPutImageAndThumbnail(ossConfProp.getUserProfileBucket(),
                                objectName, inputStream, file.getSize(), "image/png", minioUtils);

                user.setHead(response.object());
                if (optionType == 2 || optionType == 3) {
                    audit.setHead(response.object());
                }
            }
        }

        if (optionType == 1) {
            user = userRepository.save(user);
            return _prepareResponse(user);
        } else {
            identityAuditRepository.save(audit);

            // 直接返回更新后的数据，但不入库
//            return _prepareResponse(user);
            return getSelfProfile(user);
        }
    }

    @Override
    @SneakyThrows
    public User updatePhotos(User user, Integer updateType,
                             String existsPhotos, Map<String, MultipartFile> multipartFileMap) throws ApiException {

        // 操作模式， 1: 普通资料更新，2: 待审核资料更新 3: 提交审核
        int optionType;
        IdentityAudit audit = null;

        if (updateType == 1) {
            // 认证资料的修改

            // 如果已经有资料在审核
            Optional<IdentityAudit> identityAudit = identityAuditService.getLastPhotosAudit(user);

            if (identityAudit.isPresent() &&
                    identityAudit.get().getStatus() == 10) {
                // 资料审核中不能修改
                throw new ApiException(-1, "资料在认证审核中，暂时无法修改");
            }

//            if (user.getIdentity() == 1) {
//                // 主播直接提交审核
//                optionType = 3;
//                audit = identityAuditService.createAuditPrepare(user, 2);
//            } else {
//                // 非主播，直接提交待审资料
//                optionType = 2;
//                audit = identityAuditService.createAuditPrepare(user, 12);
//            }

            // 不管主播用户都直接触发审核
            optionType = 3;
            audit = identityAuditService.createAuditPrepare(user, 12);
        } else {
            optionType = 1;
        }

        List<String> photoList = new ArrayList<>();

        for (MultipartFile file: multipartFileMap.values()) {
            if (file.isEmpty()) {
                continue;
            }

            String randomUUID = UUID.randomUUID().toString();

            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            String suffixName = filename.substring(filename.lastIndexOf("."));

            if (!isPictureSupported(suffixName)) {
                throw new ApiException(-1, "图片格式不支持!");
            }

            String objectName = String.format("%s%s_%s_%s%s",
                    ossConfProp.getUserProfilePhotoPrefix(),
                    CryptoUtils.base64AesSecret(user.getUsername(), ossConfProp.getObjectAesKey()),
                    new Date().getTime(),
                    randomUUID,
                    suffixName);

            ObjectWriteResponse response = minioPutImageAndThumbnail(ossConfProp.getUserProfileBucket(),
                    objectName,
                    inputStream,
                    file.getSize(),
                    "image/png",
                    minioUtils);

            if (!StringUtils.isEmpty(response.object())) {
                photoList.add(response.object());
            }
        }

//        if (photoList.size() == 0 ) {
//            throw new ApiException(-1, "上传信息出错,请稍后重试");
//        }

        String originPhotosString = user.getPhotos();
        if (Objects.nonNull(originPhotosString) && !StringUtils.isEmpty(existsPhotos)) {
            // 过滤掉已经不需要的
            List<String> originPhotos = changePhotosToList(originPhotosString);
            List<String> existPhotos = changePhotosToList(existsPhotos);

            List<String> filterPhotos = originPhotos.stream()
                    .filter(existPhotos::contains)
                    .collect(Collectors.toList());

            if (photoList.size() > 0) {
                filterPhotos.addAll(photoList);
            }

            user.setPhotos(filterPhotos.toString());
            if (optionType == 2 || optionType == 3) {
                audit.setPhotos(filterPhotos.toString());
            }
        } else {
            if (photoList.size() > 0) {

                user.setPhotos(photoList.toString());
                if (optionType == 2 || optionType == 3) {
                    audit.setPhotos(photoList.toString());
                }
            }
        }

        if (optionType == 1) {
            user = userRepository.save(user);
            return _prepareResponse(user);
        } else {
            identityAuditRepository.save(audit);
//            return _prepareResponse(user);
            return getSelfProfile(user);
        }
    }

    @Override
    @SneakyThrows
    @Deprecated
    public User updateVerifyImage(User user,
                                  Map<String, MultipartFile> multipartFileMap) throws ApiException {

        if (user.getIdentity() == 1) {
            // TODO: 2020/11/30 如果是主播身份，不能随意更改资料，需要审核
        }

        for (MultipartFile file: multipartFileMap.values()) {
            if (file.isEmpty()) {
                continue;
            }

            String randomUUID = UUID.randomUUID().toString();

            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            String suffixName = filename.substring(filename.lastIndexOf("."));

            if (!isPictureSupported(suffixName)) {
                throw new ApiException(-1, "图片格式不支持!");
            }

            String objectName = String.format("%s%s_%s_%s%s",
                    ossConfProp.getUserProfileVerifyImagePrefix(),
                    CryptoUtils.base64AesSecret(user.getUsername(), ossConfProp.getObjectAesKey()),
                    new Date().getTime(),
                    randomUUID,
                    suffixName);

            ObjectWriteResponse response = minioPutImageAndThumbnail(ossConfProp.getUserProfileBucket(),
                    objectName,
                    inputStream,
                    file.getSize(),
                    "image/png",
                    minioUtils);

            user.setSelfie(response.object());

            // 只处理第一张能处理的照片
            break;
        }

        if (user.getSelfie() == null || user.getSelfie().isEmpty()) {
            throw new ApiException(-1, "上传信息不正确");
        }

        user = userRepository.save(user);

        return _prepareResponse(user);
    }

    @SneakyThrows
    @Override
    public User updateVideo(User user, Integer updateType,
                            Map<String, MultipartFile> multipartFileMap) throws ApiException {

        // 操作模式， 1: 普通资料更新，2: 待审核资料更新 3: 提交审核
        int optionType;
        IdentityAudit audit = null;

        if (updateType == 1) {
            // 认证资料的修改

            // 如果已经有资料在审核
            Optional<IdentityAudit> identityAudit = identityAuditService.getLastVideoAudit(user);

            if (identityAudit.isPresent() &&
                    identityAudit.get().getStatus() == 10) {
                // 资料审核中不能修改
                throw new ApiException(-1, "资料在认证审核中，暂时无法修改");
            }

//            if (user.getIdentity() == 1) {
//                // 主播直接提交审核
//                optionType = 3;
//                audit = identityAuditService.createAuditPrepare(user, 3);
//            } else {
//                // 非主播，直接提交待审资料
//                optionType = 2;
//                audit = identityAuditService.createAuditPrepare(user, 13);
//            }

            // 不管主播用户都直接触发审核
            optionType = 3;
            audit = identityAuditService.createAuditPrepare(user, 13);
        } else {
            optionType = 1;
        }

        for (MultipartFile file: multipartFileMap.values()) {
            if (file.isEmpty()) {
                continue;
            }

            String randomUUID = UUID.randomUUID().toString();

            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            String suffixName = filename.substring(filename.lastIndexOf("."));

            if (!isVideoSupported(suffixName)) {
                throw new ApiException(-1, "视频格式不支持!");
            }

            // 视频暂不提供压缩
            String objectName = String.format("%s%s_%s_%s%s",
                    ossConfProp.getUserProfileVideoPrefix(),
                    CryptoUtils.base64AesSecret(user.getUsername(), ossConfProp.getObjectAesKey()),
                    new Date().getTime(),
                    randomUUID,
                    suffixName);

            ObjectWriteResponse response = minioUtils.put(ossConfProp.getUserProfileBucket(), objectName,
                    inputStream, file.getSize(), "video/mp4");

            user.setVideo(response.object());
            if (optionType == 2 || optionType == 3) {
                audit.setVideo(response.object());
            }

            // 只处理第一个能处理的视频
            break;
        }

        if (user.getVideo() == null || user.getVideo().isEmpty()) {
            throw new ApiException(-1, "上传信息不正确");
        }

        if (optionType == 1) {
            user = userRepository.save(user);
            return _prepareResponse(user);
        } else {
            identityAuditRepository.save(audit);
//            return _prepareResponse(user);
            return getSelfProfile(user);
        }
    }

    @Override
    public int logout(User user) throws ApiException {
        // TODO: 2020/11/21 考虑登出im相关服务
        // TODO: 2020/11/26 记录登出
        return 1;
    }

//    @Cacheable(cacheNames = "anchorList", key = "#type+':'+#gender+':'+#pageIndex+':'+#pageSize")
    @SneakyThrows
    @Override
    public List<User> getAnchorList(Integer type, Integer gender, int pageIndex, int pageSize) throws ApiException {

        // 根据不同的type 做不同处理
        // type = 0 先拿推荐主播数据 如果数据不够再拿粉丝数多少排序

        // type = 1 按照总收益排序

        // type = 2 按照注册时间倒叙

//        List<User> userList =
//                userRepository.getAnchorListByCreateTime(gender, pageable).getContent();
        if (type == 0) {
//            userPage = userRepository.getAnchorListBySystemAndFollow(gender, pageable);
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            Date finalNow = dateFormat.parse(dateFormat.format(new Date()));

            if (sysConfigService.isRandRecommend()) {
                // 从 0 6 7 随机选择一个
                List<Integer> randTypeList = Arrays.asList(0, 6, 7);
                Random random = new Random();
                int n = random.nextInt(randTypeList.size());

                List<User> userList = userRepository.
                        getAnchorListBySystemAndActive(finalNow, gender, pageSize, pageSize * pageIndex, randTypeList.get(n));
                return userList.stream()
                        .map(this::_prepareResponse)
                        .collect(Collectors.toList());
            } else {
                List<User> userList = userRepository.
                        getAnchorListBySystemAndActive(finalNow, gender, pageSize, pageSize * pageIndex, 0);
                return userList.stream()
                        .map(this::_prepareResponse)
                        .collect(Collectors.toList());
            }

        } else if (type == 1) {
            // TODO: 2021/3/6 因为无需统计分页 这里可以不用pageable 减少一次sql查询
//            Pageable pageable = PageRequest.of(pageIndex, pageSize);
//            Page<User> userPage;
//            userPage = userRepository.getAnchorListByProfitEx(gender, pageable);
//            return userPage.stream()
//                    .map(this::_prepareResponse)
//                    .collect(Collectors.toList());

            // 修改成根据活跃排序
            Date now = new Date();
            Date tenDaysAgo = new Date(now.getTime() - 10 * 24 * 3600 * 1000);
            List<User> userList = userRepository.getAnchorListByActive(gender, tenDaysAgo);

            if (userList.isEmpty()) {
                // 空直接返回
                return userList;
            }

            List<String> userDigitList =
                    userList.stream()
                    .map(User::getDigitId)
                    .collect(Collectors.toList());

            // 从腾讯接口获取状态
            ImQueryStateResp imQueryStateResp = imService.queryState(userDigitList);
            List<ImQueryStateResp.QueryResult> queryResultList = imQueryStateResp.getQueryResult();
            List<String> onlineUserList = queryResultList
                    .stream()
                    .filter(queryResult -> queryResult.getStatus().equals("Online"))
                    .map(ImQueryStateResp.QueryResult::getTo_Account)
                    .collect(Collectors.toList());

            // 请求腾讯接口后返回的数据不是按照请求的顺序返回，回来的数据顺序混乱，需重新排序
            List<String> sortedOnlineUserList = userDigitList
                    .stream()
                    .filter(onlineUserList::contains)
                    .collect(Collectors.toList());

            List<String> pushOnlineUserList = queryResultList
                    .stream()
                    .filter(queryResult -> queryResult.getStatus().equals("PushOnline"))
                    .map(ImQueryStateResp.QueryResult::getTo_Account)
                    .collect(Collectors.toList());

            List<String> sortedPushOnlineUserList = userDigitList
                    .stream()
                    .filter(pushOnlineUserList::contains)
                    .collect(Collectors.toList());

            List<String> offlineUserList = queryResultList
                    .stream()
                    .filter(queryResult -> queryResult.getStatus().equals("Offline"))
                    .map(ImQueryStateResp.QueryResult::getTo_Account)
                    .collect(Collectors.toList());

            List<String> sortedOfflineUserList = userDigitList
                    .stream()
                    .filter(offlineUserList::contains)
                    .collect(Collectors.toList());

//            if (imQueryStateResp.getErrorList() != null) {
//                List<String> errorUserList = imQueryStateResp.getErrorList()
//                        .stream()
//                        .map(ImQueryStateResp.QueryResult::getTo_Account)
//                        .collect(Collectors.toList());
//            }

            // 按照 在线，推送在线，离线，出错列表的顺序构造
            List<String> sortedUserList = Stream.of(sortedOnlineUserList, sortedPushOnlineUserList, sortedOfflineUserList)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            // 分页
            int totalCount = sortedUserList.size();
            int offset = pageIndex * pageSize;
            if (offset >= totalCount) {
                // 返回空
                return new ArrayList<>();
            }
            List<String> pageUserList = sortedUserList.subList(offset, Math.min(offset + pageSize, totalCount));
            return pageUserList.stream()
                    .map(s -> userList.get(userDigitList.indexOf(s)))
                    .map(this::_prepareResponse)
                    .collect(Collectors.toList());
        } else {
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            Page<User> userPage;
            userPage = userRepository.getAnchorListByCreateTime(gender, pageable);
            return userPage.stream()
                    .map(this::_prepareResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<User> getAuditAnchorList(Integer type, Integer gender, int pageIndex, int pageSize) throws ApiException {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<User> userPage = userRepository.getAuditAnchorList(gender, pageable);

        return userPage.stream()
                .map(this::_prepareResponse)
                .collect(Collectors.toList());
    }

    @Override
    public User getAnchorProfile(User user, User selfUser) throws ApiException {
        Long selfUserId = 0L;
        if (Objects.nonNull(selfUser)) {
            selfUserId = selfUser.getId();
        }

        User finalUser = profile(user);

        // 获取屏蔽相关信息
        List<Integer> types = Arrays.asList(10, 20);
        List<Block> blockList = blockRepository
                .findByUserIdAndBlockUserIdAndTypeIsIn(selfUserId, finalUser.getId(), types);

        blockList.forEach(block -> {
            if (block.getBlock() == 1 && block.getType() == 10) {
                finalUser.setBlockDynamic(1);
            } else {
                finalUser.setBlockDynamic(0);
            }

            if (block.getBlock() == 1 && block.getType() == 20) {
                finalUser.setBlockChat(1);
            } else {
                finalUser.setBlockChat(0);
            }
        });

        Follow follow = followRepository.findByUserIdFromAndUserIdTo(selfUserId, finalUser.getId());
        if (Objects.nonNull(follow) && follow.getStatus() == 1) {
            finalUser.setIsFollow(1);
        } else {
            finalUser.setIsFollow(0);
        }

        return finalUser;
    }

    @Override
    public List<User> getRandAnchorList(int count) throws ApiException {
        return userRepository.findRandAnchorList(count);
    }

    @SneakyThrows
    @Override
    public Integer callPreCheck(User fromUser, User toUser, int type) throws ApiException {
        // 拨打是用户，接听是用户
        if (fromUser.getIdentity() == 0 && toUser.getIdentity() == 0) {
            // 对方不是主播无法发起通话
            return -1;
        }

        // 拨打是主播，接听也是主播
        if (fromUser.getIdentity() == 1 && toUser.getIdentity() == 1) {
            // 都是主播无法发起通话
            return -5;
        }

        BigDecimal callPrice;
        List<Coupon> couponList;
        Asset asset;

        if (toUser.getIdentity() == 1 ) {
            // 接听是主播

            // 判断是否休息
            if (toUser.getRest() == 1) {
                Date now = new Date();
                if (isTimeBetween(now, toUser.getRestStartDate(), toUser.getRestEndDate())) {
                    // 在排班时间内
                    return -4;
                }
            }

            // 判断主播接听类型是否支持
            if ((type == 0 && toUser.getMode() == 2) ||
                    (type == 1 && toUser.getMode() == 1)) {
                // 主播通话类型不支持
                return -3;
            }

            callPrice = type == 0 ? toUser.getCallPrice() : toUser.getVideoPrice();
            asset = assetRepository.findByUserId(fromUser.getId());
            couponList = couponService.getCallFreeCoupon(fromUser, type);
        } else {
            // 接听是用户
            callPrice = type == 0 ? fromUser.getCallPrice() : fromUser.getVideoPrice();
            asset = assetRepository.findByUserId(toUser.getId());
            couponList = couponService.getCallFreeCoupon(toUser, type);
        }

        if (Objects.isNull(callPrice)) {
            return 1;
        }

        for (Coupon coupon :
                couponList) {
            if (couponService.isCouponValid(coupon)) {
                return 1;
            }
        }

        if (asset.getCoin().compareTo(callPrice) >= 0) {
            return 1;
        }

        if (toUser.getIdentity() == 0) {
            // 对方余额不足，无法接听通话
            return -2;
        } else {
            // 当前自己余额不足，无法发起通话
            return 0;
        }
    }

    // TODO: 2020/12/30 整理到其他地方
    // minio 存储原始图片和缩略图
    @SneakyThrows
    public static ObjectWriteResponse minioPutImageAndThumbnail(String bucketName,
                                                                String objectName,
                                                                InputStream inputStream,
                                                                Long size,
                                                                String contentType,
                                                                MinioUtils minioUtils) throws ApiException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        // mark top
        bufferedInputStream.mark(Integer.MAX_VALUE);

        ObjectWriteResponse response = minioUtils.put(bucketName, objectName,
                bufferedInputStream, size, contentType);

        if (response.object().isEmpty()) {
            throw new ApiException(-1, "存储缩略图失败!");
        }

        // reset stream to mark position
        bufferedInputStream.reset();

        // 压缩图片
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (size > 1024 * 1024 * 2) {
            // 2m 以上 大小缩小一半，质量为原始质量的0.2
            Thumbnails.of(bufferedInputStream).scale(0.5).outputQuality(0.4).toOutputStream(outputStream);
        } else if (size > 1024 * 1024) {
            // 1m ～ 2m, 大小缩小到原来的0.75，质量为原始质量的0.2
            Thumbnails.of(bufferedInputStream).scale(0.75).outputQuality(0.4).toOutputStream(outputStream);
        } else if (size > 1024 * 1024 * 0.5) {
            // 500k ~1m
            Thumbnails.of(bufferedInputStream).scale(0.75f).outputQuality(0.6).toOutputStream(outputStream);
        } else if (size > 1024 * 1024 * 0.1) {
            // 100k ~ 500k
            Thumbnails.of(bufferedInputStream).scale(1.0f).outputQuality(0.6).toOutputStream(outputStream);
        } else {
            // 100k ~ 500k
            Thumbnails.of(bufferedInputStream).scale(1.0f).outputQuality(1.0).toOutputStream(outputStream);
        }

        // 存储压缩图片
        InputStream thumbnailStream = new ByteArrayInputStream(outputStream.toByteArray());
        String thumbnailObjectName = String.format("thumbnail_%s", objectName);
        ObjectWriteResponse responseThumbnail = minioUtils.put(bucketName, thumbnailObjectName,
                thumbnailStream, outputStream.size(), contentType);

        if (responseThumbnail.object().isEmpty()) {
            throw new ApiException(-1, "存储缩略图失败!");
        }

        // 返回存储原始图的response
        return response;
    }

    /**
     * 准备返回给前端的数据
     */
    private User _prepareResponse(User user) {
        // 通过生日计算年龄和星座
        user.setAge(DateUtils.getAgeFromBirth(user.getBirth()));
        user.setConstellation(DateUtils.getConstellationFromBirth(user.getBirth()));

        // 返回完整的照片的链接和缩略图的链接
        if (user.getPhotos() != null) {
            List<String> photosNameList = changePhotosToList(user.getPhotos());
            List<String> photosUrlList = photosNameList.stream()
                    .map( name -> String.format("%s/%s/%s",
                            ossConfProp.getMinioVisitUrl(), ossConfProp.getUserProfileBucket(), name))
                    .collect(Collectors.toList());
            List<String> photosThumbnailUrlList = photosNameList.stream()
                    .map( name -> String.format("%s/%s/thumbnail_%s",
                            ossConfProp.getMinioVisitUrl(), ossConfProp.getUserProfileBucket(), name))
                    .collect(Collectors.toList());
            user.setPhotoUrlList(photosUrlList);
            user.setPhotoThumbnailUrlList(photosThumbnailUrlList);
        }

        // 返回完整的头像的链接和缩略图的链接
        if (user.getHead() != null && !user.getHead().isEmpty()) {
            String headUrl = String.format("%s/%s/%s",
                    ossConfProp.getMinioVisitUrl(),
                    ossConfProp.getUserProfileBucket(),
                    user.getHead());
            String headThumbnailUrl = String.format("%s/%s/thumbnail_%s",
                    ossConfProp.getMinioVisitUrl(),
                    ossConfProp.getUserProfileBucket(),
                    user.getHead());
            user.setHeadUrl(headUrl);
            user.setHeadThumbnailUrl(headThumbnailUrl);
        }

        // 返回完整的语音介绍的链接
        if (user.getVoice() != null && !user.getVoice().isEmpty()) {
            String voiceUrl = String.format("%s/%s/%s",
                    ossConfProp.getMinioVisitUrl(),
                    ossConfProp.getUserProfileBucket(),
                    user.getVoice());
            user.setVoiceUrl(voiceUrl);
        }

        // 返回完整的认证图片链接和缩略图的链接
//        if (user.getSelfie() != null && !user.getSelfie().isEmpty()) {
//            String selfieUrl = String.format("%s/%s", ossConfProp.getMinioUrl(), user.getSelfie());
//            String selfieThumbnailUrl = String.format("%s/thumbnail_%s", ossConfProp.getMinioUrl(), user.getSelfie());
//            user.setSelfieUrl(selfieUrl);
//            user.setSelfieThumbnailUrl(selfieThumbnailUrl);
//        }

        // 返回完整的视频链接
        if (user.getVideo() != null && !user.getVideo().isEmpty()) {
            String videoUrl = String.format("%s/%s/%s",
                    ossConfProp.getMinioVisitUrl(),
                    ossConfProp.getUserProfileBucket(),
                    user.getVideo());
            user.setVideoUrl(videoUrl);
        }

        if (Objects.nonNull(user.getVideoPrice())) {
            user.setVideoPrice(user.getVideoPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
            user.setVideoPrice(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        if (Objects.nonNull(user.getCallPrice())) {
            user.setCallPrice(user.getCallPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
            user.setCallPrice(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        if (Objects.nonNull(user.getMessagePrice())) {
            user.setMessagePrice(user.getMessagePrice().setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
            user.setMessagePrice(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP));
        }

        // 是否休息判断
        user.setCurrRest(0);
        if (user.getRest() == 1) {
            Date now = new Date();
            if (isTimeBetween(now, user.getRestStartDate(), user.getRestEndDate())) {
                // 在排班时间内
                user.setCurrRest(1);
            }
        }

        return user;
    }

    /**
     * 讲photos字符串转化成list
     */
    public static List<String> changePhotosToList(String photos) {
        return Arrays.stream(photos.split("\\[|\\]|,"))
                .filter(s -> !s.isEmpty())
                .map(String::trim)
                .collect(Collectors.toList());
    }

    // 创建标签
    // TODO: 2021/4/2 放到lable service 实现
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

    /**
     * 8数字id： 1位年，2020-2028分别为（1 - 9)， 4位为当前年内的小时数 最大 366*25，3位为每个小时内的用户注册数
     * 从2020年开始到2028年暂时可以了，真能到那一天数字id可以为9位或者10位了
     */
    protected static String calcDigitId(LocalDateTime dateTime, long index) {
        // 从2020年开始计算
        final int startYear = 2020;
        final int maxIndex = 999;

        int year = dateTime.getYear();
        int yearIndex = year - startYear + 1;

        int dayOfYear = dateTime.getDayOfYear();
        int hour = dateTime.getHour() + 1;
        int hourOfYear = dayOfYear * hour;

        // TODO: 2020/11/9 记录一些异常情况
        if (yearIndex <= 0) {
            //
        }

        if (index > maxIndex) {

        }


        if (index > maxIndex) {
            // 如果每小时注册超过999，则生成总共9位的数字id，每小时注册上限变成9999
            return String.format("%01d%04d%04d", yearIndex, hourOfYear, index);
        } else {
            return String.format("%01d%04d%03d", yearIndex, hourOfYear, index);
        }
    }

    protected static String calcDigitId(Date date, long index) {

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);

        // 从2020年开始计算
        final int startYear = 2020;
        final int maxIndex = 999;

        int year = gregorianCalendar.get(Calendar.YEAR);
        int yearIndex = year - startYear + 1;

        int dayOfYear = gregorianCalendar.get(Calendar.DAY_OF_YEAR);
        int hour = gregorianCalendar.get(Calendar.HOUR_OF_DAY) + 1;
//        int am_pm = gregorianCalendar.get(Calendar.AM_PM);
//        if (am_pm == 1) {
//            hour += 12;
//        }
        int hourOfYear = dayOfYear * 24 + hour;

        // TODO: 2020/11/9 记录一些异常情况
        if (yearIndex <= 0) {
            //
        }

        if (index > maxIndex) {

        }


        if (index > maxIndex) {
            // 如果每小时注册超过999，则生成总共9位的数字id，每小时注册上限变成9999
            return String.format("%01d%04d%04d", yearIndex, hourOfYear, index);
        } else {
            return String.format("%01d%04d%03d", yearIndex, hourOfYear, index);
        }
    }

    // 以天为单位
    protected static String calcDigitIdV2(Date date, long index) {

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date);

        // 从2020年开始计算
        final int startYear = 2020;
        final int maxIndex = 9999;

        int year = gregorianCalendar.get(Calendar.YEAR);
        int yearIndex = year - startYear + 1;

        int dayOfYear = gregorianCalendar.get(Calendar.DAY_OF_YEAR);

        // 考虑跟以前的不冲突,这里从大到小
        dayOfYear = 999 - dayOfYear;

        // TODO: 2020/11/9 记录一些异常情况
        if (yearIndex <= 0) {
            //
        }

        if (index > maxIndex) {

        }


        if (index > maxIndex) {
            // 如果每小时注册超过9999，则生成总共9位的数字id，每小时注册上限变成99999
            return String.format("%01d%03d%05d", yearIndex, dayOfYear, index);
        } else {
            return String.format("%01d%03d%04d", yearIndex, dayOfYear, index);
        }
    }

    public static boolean isPictureSupported(String stuff) {
        return stuff.toLowerCase().equals(".jpg") ||
                stuff.toLowerCase().equals(".jpeg") ||
                stuff.toLowerCase().equals(".png");
    }

    public static boolean isVideoSupported(String stuff) {
//        return stuff.toLowerCase().equals(".mp4") ||
//                stuff.toLowerCase().equals(".avi");

        return true;
    }

    public static boolean isAudioSupported(String stuff) {
//        return stuff.toLowerCase().equals(".mp3");
        return true;
    }

    // 判断"HH:mm"时间是否在某个时间段（可能跨天）
    @SneakyThrows
    private static boolean isTimeBetween(Date time, Date startTime, Date endTime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date finalNow = dateFormat.parse(dateFormat.format(time));

        Date dayEnd = dateFormat.parse("23:59");
        Date dayStart = dateFormat.parse("00:00");

        if (Objects.nonNull(startTime) && Objects.nonNull(endTime)) {
            if (startTime.before(endTime)) {
                // 不超过24点
                if ((finalNow.after(startTime) || finalNow.equals(startTime)) &&
                        finalNow.before(endTime)) {
                    return true;
                }
            } else {
                // 超过24点
                if ((finalNow.after(startTime) || finalNow.equals(startTime) && finalNow.before(dayEnd)) ||
                        (finalNow.after(dayStart) || finalNow.equals(dayStart)) && finalNow.before(endTime)) {
                    return true;
                }
            }
        } else {
            return true;
        }

        return false;
    }
}
