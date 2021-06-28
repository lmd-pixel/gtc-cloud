package com.fmisser.gtc.social.service.impl;

import com.fmisser.fpp.oss.cos.service.CosService;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.social.service.CommonService;
import lombok.AllArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author by fmisser
 * @create 2021/6/24 11:27 上午
 * @description TODO
 */
@Service
@AllArgsConstructor
public class CommonServiceImpl implements CommonService {
    private final OssConfProp ossConfProp;
    private final CosService cosService;

    @Override
    public Optional<Pair<String, String>> getUserProfileHeadCompleteUrl(String head) throws ApiException {
        if (StringUtils.isEmpty(head)) {
            return Optional.empty();
        }

        String origin_tail = "!0";  // 1080p
        String thumbnail_tail = "!head"; // 300x300

        String originUrl = String.format("%s/%s%s%s",
                cosService.getDomainName(ossConfProp.getCosCdn(),
                        ossConfProp.getUserDynamicCosBucket()),
                ossConfProp.getCosUserProfileRootPath(), head, origin_tail);
        String thumbnailUrl = String.format("%s/%s%s%s",
                cosService.getDomainName(ossConfProp.getCosCdn(),
                        ossConfProp.getUserDynamicCosBucket()),
                ossConfProp.getCosUserProfileRootPath(), head, thumbnail_tail);

        return Optional.of(Pair.of(originUrl, thumbnailUrl));
    }

    @Override
    public Optional<Pair<List<String>, List<String>>> getUserProfilePhotosCompleteUrl(List<String> photos) throws ApiException {
        if (Objects.isNull(photos) || photos.size() == 0) {
            return Optional.empty();
        }

        List<String> photosUrlList = photos.stream()
                .map(p -> getPhotoCompleteUrl(p, ossConfProp.getCosUserProfileRootPath()))
                .collect(Collectors.toList());
        List<String> photosThumbnailUrlList = photos.stream()
                .map(p -> getThumbnailPhotoCompleteUrl(p, ossConfProp.getCosUserProfileRootPath()))
                .collect(Collectors.toList());

        return Optional.of(Pair.of(photosUrlList, photosThumbnailUrlList));
    }

    @Override
    public Optional<String> getUserProfileVoiceCompleteUrl(String voice) throws ApiException {
        if (StringUtils.isEmpty(voice)) {
            return Optional.empty();
        }

        String originUrl = String.format("%s/%s%s",
                cosService.getDomainName(ossConfProp.getCosCdn(),
                        ossConfProp.getUserDynamicCosBucket()),
                ossConfProp.getCosUserProfileRootPath(), voice);

        return Optional.of(originUrl);
    }

    @Override
    public Optional<String> getUserProfileVideoCompleteUrl(String video) throws ApiException {
        if (StringUtils.isEmpty(video)) {
            return Optional.empty();
        }

        String pureName = video.substring(0, video.lastIndexOf('.'));
        String thumbnailPurlName = StringUtils.replace(pureName, ossConfProp.getCosUserProfileVideoPrefix(),
                ossConfProp.getUserProfileVideoThumbnailPrefix());

        String url = String.format("%s/%s%s.mp4",
                cosService.getDomainName(ossConfProp.getCosCdn(), ossConfProp.getUserDynamicCosBucket()),
                ossConfProp.getCosUserProfileRootPath(), thumbnailPurlName);

        return Optional.of(url);
    }

    @Override
    public Optional<Pair<String, String>> getUserDynamicVideoCompleteUrl(String video) throws ApiException {
        if (StringUtils.isEmpty(video)) {
            return Optional.empty();
        }

        String pureName = video.substring(0, video.lastIndexOf('.'));
        String thumbnailPurlName = StringUtils.replace(pureName, ossConfProp.getCosUserProfileVideoPrefix(),
                ossConfProp.getUserProfileVideoThumbnailPrefix());

        String originUrl = String.format("%s/%s%s.mp4",
                cosService.getDomainName(ossConfProp.getCosCdn(), ossConfProp.getUserDynamicCosBucket()),
                ossConfProp.getCosUserDynamicRootPath(), thumbnailPurlName);

        String thumbnailUrl =  String.format("%s/%s%s_mute.mp4",
                cosService.getDomainName(ossConfProp.getCosCdn(), ossConfProp.getUserDynamicCosBucket()),
                ossConfProp.getCosUserDynamicRootPath(), thumbnailPurlName);

        return Optional.of(Pair.of(originUrl, thumbnailUrl));
    }

    @Override
    public String getSysImageCompleteUrl(String image) throws ApiException {
        return String.format("%s/%s%s",
                cosService.getDomainName(ossConfProp.getCosCdn(),
                        ossConfProp.getUserDynamicCosBucket()),
                ossConfProp.getCosSystemConfigRootPath(), image);
    }

    @Override
    public String getPhotoCompleteUrl(String photo, String rootPath) throws ApiException {
        String origin_tail = "!0";

        return String.format("%s/%s%s%s",
                cosService.getDomainName(ossConfProp.getCosCdn(),
                        ossConfProp.getUserDynamicCosBucket()), rootPath, photo, origin_tail);
    }

    @Override
    public String getThumbnailPhotoCompleteUrl(String photo, String rootPath) throws ApiException {
        String thumbnail_tail = "!gsv";

        return String.format("%s/%s%s%s",
                cosService.getDomainName(ossConfProp.getCosCdn(),
                        ossConfProp.getUserDynamicCosBucket()), rootPath, photo, thumbnail_tail);
    }
}
