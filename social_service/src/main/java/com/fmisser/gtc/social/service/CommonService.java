package com.fmisser.gtc.social.service;

import com.fmisser.gtc.base.exception.ApiException;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

/**
 * @author by fmisser
 * @create 2021/6/24 11:24 上午
 * @description TODO
 */
public interface CommonService {
    // 返回高清以及缩略图的完整url
    Optional<Pair<String, String>> getUserProfileHeadCompleteUrl(String head) throws ApiException;

    Optional<Pair<List<String>, List<String>>> getUserProfilePhotosCompleteUrl(List<String> photos) throws ApiException;

    Optional<String> getUserProfileVoiceCompleteUrl(String voice) throws ApiException;

    Optional<String> getUserProfileVideoCompleteUrl(String video) throws ApiException;

    Optional<Pair<String, String>> getUserDynamicVideoCompleteUrl(String video) throws ApiException;

    String getSysImageCompleteUrl(String image) throws ApiException;

    String getPhotoCompleteUrl(String photo, String rootPath) throws ApiException;
    String getThumbnailPhotoCompleteUrl(String photo, String rootPath) throws ApiException;
}
