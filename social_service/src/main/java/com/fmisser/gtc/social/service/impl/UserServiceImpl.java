package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.base.response.ApiRespHelper;
import com.fmisser.gtc.base.utils.CryptoUtils;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.controller.BlockController;
import com.fmisser.gtc.social.domain.*;
import com.fmisser.gtc.social.repository.*;
import com.fmisser.gtc.social.service.IdentityAuditService;
import com.fmisser.gtc.social.service.UserService;
import com.fmisser.gtc.social.utils.MinioUtils;
import io.minio.ObjectWriteResponse;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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

    public UserServiceImpl(UserRepository userRepository,
                           MinioUtils minioUtils,
                           LabelRepository labelRepository,
                           OssConfProp ossConfProp,
                           IdentityAuditService identityAuditService,
                           AssetRepository assetRepository,
                           BlockRepository blockRepository,
                           FollowRepository followRepository) {
        this.userRepository = userRepository;
        this.minioUtils = minioUtils;
        this.labelRepository = labelRepository;
        this.ossConfProp = ossConfProp;
        this.identityAuditService = identityAuditService;
        this.assetRepository = assetRepository;
        this.blockRepository = blockRepository;
        this.followRepository = followRepository;
    }

    @Transactional
    @Override
    public User create(String phone, int gender) throws ApiException {
        // check exist
        if (userRepository.existsByUsername(phone)) {
            // 已经存在
            throw new ApiException(-1, "用户名重复");
        }

        // create
        User user = new User();
        user.setUsername(phone);
        user.setPhone(phone);

        LocalDateTime dateTime = LocalDateTime.now();
        Date startHour = Date.from(DateUtils.getHourStart(dateTime).atZone(ZoneId.systemDefault()).toInstant());
        Date endHour = Date.from(DateUtils.getHourEnd(dateTime).atZone(ZoneId.systemDefault()).toInstant());
        long count = userRepository.countByCreateTimeBetween(startHour, endHour);
        String digitId = calcDigitId(dateTime, count + 1);
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

        return _prepareResponse(user);
    }

    @Override
    public User getUserByUsername(String username) throws ApiException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new ApiException(-1, "用户数据不存在");
        }
        return userOptional.get();
    }

    @Override
    public User getUserByDigitId(String digitId) throws ApiException {
        Optional<User> userOptional = userRepository.findByDigitId(digitId);
        if (!userOptional.isPresent()) {
            throw new ApiException(-1, "用户数据不存在");
        }
        return userOptional.get();
    }

    @Override
    public User profile(User user) throws ApiException {
        return _prepareResponse(user);
    }

    @SneakyThrows
    @Override
    public User updateProfile(User user,
                              String nick, String birth, String city, String profession,
                              String intro, String labels, String callPrice, String videoPrice,
                              Map<String, MultipartFile> multipartFileMap) throws ApiException {

        if (user.getIdentity() == 1) {
            // TODO: 2020/11/30 如果是主播身份，不能随意更改资料，需要审核
        }
        
        Optional<IdentityAudit> identityAudit = identityAuditService.getLastProfileAudit(user);
        if (identityAudit.isPresent() &&
                identityAudit.get().getStatus() == 10) {
            // 资料审核中不能修改
            throw new ApiException(-1, "资料在认证审核中，暂时无法修改");
        }

        // 处理文本结构
        if (nick != null && !nick.isEmpty()) {
            user.setNick(nick);
        }
        if (birth != null && !birth.isEmpty()) {
            user.setBirth(new Date(Long.parseLong(birth)));
        }
        if (city != null && !city.isEmpty()) {
            user.setCity(city);
        }
        if (profession != null && !profession.isEmpty()) {
            user.setProfession(profession);
        }
        if (intro != null && !intro.isEmpty()) {
            user.setIntro(intro);
        }
        if (labels != null && !labels.isEmpty()) {
            String[] labelList = labels.split(",");
            user.setLabels(_innerCreateLabels(labelList));
        }
        if (callPrice != null && !callPrice.isEmpty()) {
            BigDecimal price = BigDecimal.valueOf(Long.parseLong(callPrice));
            user.setCallPrice(price);
        }
        if (videoPrice != null && !videoPrice.isEmpty()) {
            BigDecimal price = BigDecimal.valueOf(Long.parseLong(videoPrice));
            user.setVideoPrice(price);
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
            }
        }

        user = userRepository.save(user);

        return _prepareResponse(user);
    }

    @Override
    @SneakyThrows
    public User updatePhotos(User user,
                             Map<String, MultipartFile> multipartFileMap) throws ApiException {
        if (user.getIdentity() == 1) {
            // TODO: 2020/11/30 如果是主播身份，不能随意更改资料，需要审核
        }

        Optional<IdentityAudit> identityAudit = identityAuditService.getLastPhotosAudit(user);

        if (identityAudit.isPresent() &&
                identityAudit.get().getStatus() == 10) {
            // 资料审核中不能修改
            throw new ApiException(-1, "资料在认证审核中，暂时无法修改");
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

        if (photoList.size() == 0 ) {
            throw new ApiException(-1, "上传信息出错,请稍后重试");
        }

        user.setPhotos(photoList.toString());
        user = userRepository.save(user);

        return _prepareResponse(user);
    }

    @Override
    @SneakyThrows
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
    public User updateVideo(User user, Map<String, MultipartFile> multipartFileMap) throws ApiException {

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

            // 只处理第一个能处理的视频
            break;
        }

        if (user.getVideo() == null || user.getVideo().isEmpty()) {
            throw new ApiException(-1, "上传信息不正确");
        }

        user = userRepository.save(user);

        return _prepareResponse(user);
    }

    @Override
    public int logout(User user) throws ApiException {
        // TODO: 2020/11/21 考虑登出im相关服务
        // TODO: 2020/11/26 记录登出
        return 1;
    }

    @Override
    public List<User> getAnchorList(Integer type, Integer gender, int pageIndex, int pageSize) throws ApiException {

        // 根据不同的type 做不同处理
        // type = 0 先拿推荐主播数据 如果数据不够再拿粉丝数多少排序

        // type = 1 按照总收益排序

        // type = 2 按照注册时间倒叙

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<User> userPage;
//        List<User> userList =
//                userRepository.getAnchorListByCreateTime(gender, pageable).getContent();
        if (type == 0) {
            userPage = userRepository.getAnchorListBySystemAndFollow(gender, pageable);
        } else if (type == 1) {
            userPage = userRepository.getAnchorListByProfit(gender, pageable);
        } else {
            userPage = userRepository.getAnchorListByCreateTime(gender, pageable);
        }
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
            // 2m 以上
            Thumbnails.of(bufferedInputStream).scale(0.5).outputQuality(0.2).toOutputStream(outputStream);
        } else if (size > 1024 * 1024) {
            // 1m ～ 2m
            Thumbnails.of(bufferedInputStream).scale(0.75).outputQuality(0.2).toOutputStream(outputStream);
        } else if (size > 1024 * 1024 * 0.5) {
            // 500k ~1m
            Thumbnails.of(bufferedInputStream).scale(0.75f).outputQuality(0.3).toOutputStream(outputStream);
        } else if (size > 1024 * 1024 * 0.1) {
            // 100k ~ 500k
            Thumbnails.of(bufferedInputStream).scale(1.0f).outputQuality(0.3).toOutputStream(outputStream);
        } else {
            // 100k ~ 500k
            Thumbnails.of(bufferedInputStream).scale(1.0f).outputQuality(0.5).toOutputStream(outputStream);
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
                            ossConfProp.getMinioUrl(), ossConfProp.getUserProfileBucket(), name))
                    .collect(Collectors.toList());
            List<String> photosThumbnailUrlList = photosNameList.stream()
                    .map( name -> String.format("%s/%s/thumbnail_%s",
                            ossConfProp.getMinioUrl(), ossConfProp.getUserProfileBucket(), name))
                    .collect(Collectors.toList());
            user.setPhotoUrlList(photosUrlList);
            user.setPhotoThumbnailUrlList(photosThumbnailUrlList);
        }

        // 返回完整的头像的链接和缩略图的链接
        if (user.getHead() != null && !user.getHead().isEmpty()) {
            String headUrl = String.format("%s/%s/%s",
                    ossConfProp.getMinioUrl(),
                    ossConfProp.getUserProfileBucket(),
                    user.getHead());
            String headThumbnailUrl = String.format("%s/%s/thumbnail_%s",
                    ossConfProp.getMinioUrl(),
                    ossConfProp.getUserProfileBucket(),
                    user.getHead());
            user.setHeadUrl(headUrl);
            user.setHeadThumbnailUrl(headThumbnailUrl);
        }

        // 返回完整的语音介绍的链接
        if (user.getVoice() != null && !user.getVoice().isEmpty()) {
            String voiceUrl = String.format("%s/%s/%s",
                    ossConfProp.getMinioUrl(),
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
                    ossConfProp.getMinioUrl(),
                    ossConfProp.getUserProfileBucket(),
                    user.getVideo());
            user.setVideoUrl(videoUrl);
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
        return stuff.toLowerCase().equals(".mp3");
    }
}
