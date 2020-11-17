package com.fmisser.gtc.social.service.impl;

import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.base.response.ApiRespHelper;
import com.fmisser.gtc.base.utils.CryptoUtils;
import com.fmisser.gtc.base.utils.DateUtils;
import com.fmisser.gtc.social.domain.Asset;
import com.fmisser.gtc.social.domain.Label;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.domain.VerifyStatus;
import com.fmisser.gtc.social.repository.LabelRepository;
import com.fmisser.gtc.social.repository.UserRepository;
import com.fmisser.gtc.social.service.UserService;
import com.fmisser.gtc.social.utils.MinioUtils;
import io.minio.ObjectWriteResponse;
import jdk.internal.util.xml.impl.Input;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ApiRespHelper apiRespHelper;

    private final MinioUtils minioUtils;

    private final LabelRepository labelRepository;

    private final OssConfProp ossConfProp;

    public UserServiceImpl(UserRepository userRepository,
                           ApiRespHelper apiRespHelper,
                           MinioUtils minioUtils,
                           LabelRepository labelRepository,
                           OssConfProp ossConfProp) {
        this.userRepository = userRepository;
        this.apiRespHelper = apiRespHelper;
        this.minioUtils = minioUtils;
        this.labelRepository = labelRepository;
        this.ossConfProp = ossConfProp;
    }

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

        user.setAsset(new Asset());
        user.setLabels(new ArrayList<>());
        user.setVerifyStatus(new VerifyStatus());

        user = userRepository.save(user);
        if (user.getId() > 0) {
            // success
            return _prepareResponse(user);
        } else {
            // 创建失败
            throw new ApiException(-1, "创建用户信息失败");
        }
    }

    @Override
    public User profile(String username) throws ApiException {
        User user = userRepository.findByUsername(username);
        return _prepareResponse(user);
    }

    @SneakyThrows
    @Override
    public User updateProfile(String username, String nick, String birth, String city, String profession,
                                       String intro, String labels, String callPrice, String videoPrice,
                                       Map<String, MultipartFile> multipartFileMap) throws ApiException {
        User userDo = userRepository.findByUsername(username);
        if (userDo == null) {
            throw new ApiException(-1, "用户数据不存在");
        }

        // 处理文本结构
        if (nick != null && !nick.isEmpty()) {
            userDo.setNick(nick);
        }
        if (birth != null && !birth.isEmpty()) {
            userDo.setBirth(new Date(Long.parseLong(birth)));
        }
        if (city != null && !city.isEmpty()) {
            userDo.setCity(city);
        }
        if (profession != null && !profession.isEmpty()) {
            userDo.setProfession(profession);
        }
        if (intro != null && !intro.isEmpty()) {
            userDo.setIntro(intro);
        }
        if (labels != null && !labels.isEmpty()) {
            String[] labelList = labels.split(",");
            userDo.setLabels(_innerCreateLabels(labelList));
        }
        if (callPrice != null && !callPrice.isEmpty()) {
            BigDecimal price = BigDecimal.valueOf(Long.parseLong(callPrice));
            userDo.setCallPrice(price);
        }
        if (videoPrice != null && !videoPrice.isEmpty()) {
            BigDecimal price = BigDecimal.valueOf(Long.parseLong(videoPrice));
            userDo.setVideoPrice(price);
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
                        CryptoUtils.base64AesSecret(userDo.getUsername(), ossConfProp.getObjectAesKey()),
                        new Date().getTime(),
                        randomUUID,
                        suffixName);
                ObjectWriteResponse response = minioUtils.put(ossConfProp.getUserProfileBucket(), objectName,
                        inputStream, file.getSize(), "audio/mpeg");

                if (response.object().isEmpty()) {
                    throw new ApiException(-1, "存储语音文件失败!");
                }

                userDo.setVoice(response.object());

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
                        CryptoUtils.base64AesSecret(userDo.getUsername(), ossConfProp.getObjectAesKey()),
                        new Date().getTime(),
                        randomUUID,
                        suffixName);

                ObjectWriteResponse response = _minioPutImageAndThumbnail(ossConfProp.getUserProfileBucket(),
                        objectName, inputStream, file.getSize(), "image/png");

                userDo.setHead(response.object());
            }
//            else if (name.equals("video")) {
//                InputStream inputStream = file.getInputStream();
//                String filename = file.getOriginalFilename();
//                String suffixName = filename.substring(filename.lastIndexOf("."));
//                // TODO: 2020/11/9 判断文件类型是否满足需要
//                // TODO: 2020/11/9 压缩并分别存储压缩后和原始文件
//                String objectName = String.format("media/video/%s/min_video_%s%s",
//                        userDo.getUsername(), String.valueOf(new Date().getTime()), suffixName);
//                ObjectWriteResponse response = minioUtils.put("user-profiles", objectName,
//                        inputStream, file.getSize(), "video/mp4");
//                System.out.println(response);
//            }
        }

        // 判断资料是否完全录入
        if (_checkProfileCompleted(userDo)) {
            userDo.getVerifyStatus().setProfileStatus(10);
        } else {
            userDo.getVerifyStatus().setProfileStatus(0);
        }

        userDo = userRepository.save(userDo);

        return _prepareResponse(userDo);
    }

    @Override
    @SneakyThrows
    public User updatePhotos(String username,
                                              Map<String, MultipartFile> multipartFileMap) throws ApiException {

        User userDo = userRepository.findByUsername(username);
        if (userDo == null) {
            throw new ApiException(-1, "用户数据不存在");
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
                    CryptoUtils.base64AesSecret(userDo.getUsername(), ossConfProp.getObjectAesKey()),
                    new Date().getTime(),
                    randomUUID,
                    suffixName);

            ObjectWriteResponse response = _minioPutImageAndThumbnail(ossConfProp.getUserProfileBucket(),
                    objectName,
                    inputStream,
                    file.getSize(),
                    "image/png");

            photoList.add(response.object());
        }

        if (userDo.getPhotos() == null || userDo.getPhotos().isEmpty()) {
            throw new ApiException(-1, "上传信息不正确");
        }

        // 更新照片更新状态
        userDo.getVerifyStatus().setPhotosStatus(10);   // 照片资料完善

        userDo.setPhotos(photoList.toString());
        userDo = userRepository.save(userDo);

        return _prepareResponse(userDo);
    }

    @Override
    @SneakyThrows
    public User updateVerifyImage(String username,
                                             Map<String, MultipartFile> multipartFileMap) throws ApiException {
        User userDo = userRepository.findByUsername(username);
        if (userDo == null) {
            throw new ApiException(-1, "用户数据不存在");
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
                    CryptoUtils.base64AesSecret(userDo.getUsername(), ossConfProp.getObjectAesKey()),
                    new Date().getTime(),
                    randomUUID,
                    suffixName);

            ObjectWriteResponse response = _minioPutImageAndThumbnail(ossConfProp.getUserProfileBucket(),
                    objectName,
                    inputStream,
                    file.getSize(),
                    "image/png");

            userDo.setSelfie(response.object());

            // 只处理第一张能处理的照片
            break;
        }

        if (userDo.getSelfie() == null || userDo.getSelfie().isEmpty()) {
            throw new ApiException(-1, "上传信息不正确");
        }

        // 更新照片更新状态
        userDo.getVerifyStatus().setSelfieStatus(10);   // 照片资料完善

        userDo = userRepository.save(userDo);

        return _prepareResponse(userDo);
    }

    @Override
    public int logout() throws ApiException {
        return 1;
    }

    // minio 存储原始图片和缩略图
    @SneakyThrows
    private ObjectWriteResponse _minioPutImageAndThumbnail(String bucketName,
                                                           String objectName,
                                                           InputStream inputStream,
                                                           Long size,
                                                           String contentType) throws ApiException {
        ObjectWriteResponse response = minioUtils.put(bucketName, objectName,
                inputStream, size, contentType);

        if (response.object().isEmpty()) {
            throw new ApiException(-1, "存储缩略图失败!");
        }

        // 压缩图片
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (size > 1024 * 1024 * 2) {
            // 2m 以上
            Thumbnails.of(inputStream).scale(0.5).outputQuality(0.2).toOutputStream(outputStream);
        } else if (size > 1024 * 1024) {
            // 1m ～ 2m
            Thumbnails.of(inputStream).scale(0.75).outputQuality(0.2).toOutputStream(outputStream);
        } else if (size > 1024 * 1024 * 0.5) {
            // 500k ~1m
            Thumbnails.of(inputStream).scale(0.75f).outputQuality(0.3).toOutputStream(outputStream);
        } else if (size > 1024 * 1024 * 0.1) {
            // 100k ~ 500k
            Thumbnails.of(inputStream).scale(1.0f).outputQuality(0.3).toOutputStream(outputStream);
        } else {
            // 100k ~ 500k
            Thumbnails.of(inputStream).scale(1.0f).outputQuality(0.5).toOutputStream(outputStream);
        }

        // 存储压缩图片
        InputStream thumbnailStream = new ByteArrayInputStream(outputStream.toByteArray());
        String thumbnailObjectName = String.format("thumbnail_%s", objectName);
        response = minioUtils.put(bucketName, thumbnailObjectName,
                thumbnailStream, outputStream.size(), contentType);

        if (response.object().isEmpty()) {
            throw new ApiException(-1, "存储缩略图失败!");
        }

        // 返回存储原始图的response
        return response;
    }

    /**
     * 判断资料是否已全部完善
     */
    private boolean _checkProfileCompleted(User user) {
        return false;
    }

    /**
     * 准备返回给前端的数据
     */
    private User _prepareResponse(User user) {
        // 计算返回给前端的数据
        user.setAge(DateUtils.getAgeFromBirth(user.getBirth()));
        user.setConstellation(DateUtils.getConstellationFromBirth(user.getBirth()));
        if (user.getPhotos() != null) {
            List<String> photosName = _changePhotosToList(user.getPhotos());
            List<String> photosUrl = photosName.stream()
                    .map( name -> String.format("%s/%s", ossConfProp.getMinioUrl(), name))
                    .collect(Collectors.toList());
            List<String> photosThumbnailUrl = photosName.stream()
                    .map( name -> String.format("%s/thumbnail_%s", ossConfProp.getMinioUrl(), name))
                    .collect(Collectors.toList());
            user.setPhotoUrlList(photosUrl);
            user.setPhotoThumbnailUrlList(photosThumbnailUrl);
        }
        if (!user.getHead().isEmpty()) {
            String headUrl = String.format("%s/%s", ossConfProp.getMinioUrl(), user.getHead());
            String headThumbnailUrl = String.format("%s/thumbnail_%s", ossConfProp.getMinioUrl(), user.getHead());
            user.setHeadUrl(headUrl);
            user.setHeadThumbnailUrl(headThumbnailUrl);
        }
        if (!user.getVoice().isEmpty()) {
            String voiceUrl = String.format("%s/%s", ossConfProp.getMinioUrl(), user.getVoice());
            user.setVoiceUrl(voiceUrl);
        }
        if (!user.getSelfie().isEmpty()) {
            String selfieUrl = String.format("%s/%s", ossConfProp.getMinioUrl(), user.getSelfie());
            String selfieThumbnailUrl = String.format("%s/thumbnail_%s", ossConfProp.getMinioUrl(), user.getSelfie());
            user.setSelfieUrl(selfieUrl);
            user.setSelfieThumbnailUrl(selfieThumbnailUrl);
        }

        return user;
    }

    /**
     * 讲photos字符串转化成list
     */
    private List<String> _changePhotosToList(String photos) {
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

    protected static boolean isPictureSupported(String stuff) {
        return stuff.toLowerCase().equals(".jpg") ||
                stuff.toLowerCase().equals(".jpeg") ||
                stuff.toLowerCase().equals(".png");
    }

    protected static boolean isVideoSupported(String stuff) {
        return stuff.toLowerCase().equals(".mp4") ||
                stuff.toLowerCase().equals(".avi");
    }

    protected static boolean isAudioSupported(String stuff) {
        return stuff.toLowerCase().equals(".mp3");
    }
}
