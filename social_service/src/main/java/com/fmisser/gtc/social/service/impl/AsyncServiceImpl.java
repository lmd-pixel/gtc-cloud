package com.fmisser.gtc.social.service.impl;

//import com.fmisser.fpp.oss.cos.dto.RecognitionResult;
//import com.fmisser.fpp.oss.cos.dto.ResultInfo;
//import com.fmisser.fpp.oss.cos.dto.VideoAuditQueryResponse;
//import com.fmisser.fpp.oss.cos.dto.VideoAuditResponse;
import com.fmisser.fpp.oss.cos.service.CosService;
import com.fmisser.gtc.base.exception.ApiException;
import com.fmisser.gtc.base.prop.OssConfProp;
import com.fmisser.gtc.social.domain.Moderation;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.service.AsyncService;
import com.fmisser.gtc.social.service.ImService;
import com.fmisser.gtc.social.service.ModerationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author by fmisser
 * @create 2021/5/22 11:36 上午
 * @description 异步任务实现类
 */

@Slf4j
@Service
@AllArgsConstructor
public class AsyncServiceImpl implements AsyncService {

    private final ImService imService;
    private final CosService cosService;
    private final OssConfProp ossConfProp;
    private final ModerationService moderationService;

//    @Async("async-task-exec")
    @Async
    @Override
    public CompletableFuture<Integer> setProfileAsync(User user, Long delayMills) throws ApiException {
        if (delayMills > 0) {
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                log.error("[async] set profile async exception when thread sleep...");
                throw new ApiException(-1, "内部异常");
            }
        }

        return CompletableFuture.completedFuture(imService.setProfile(user));
    }

//    @Async
//    @Override
//    public CompletableFuture<Integer> dynamicPicAuditAsync(Long dynamicId, List<String> pics) throws ApiException {
//
//        for (String pic: pics) {
//            String url = cosService.getPictureAuditDomainName(ossConfProp.getUserDynamicCosBucket());
//            String objectPath = String.format("%s%s", ossConfProp.getCosUserDynamicRootPath(), pic);
//            RecognitionResult recognitionResult = cosService.recognizePicture(url, objectPath);
//            wrapperResult(recognitionResult);
//
//            List<Moderation> moderationList = moderationService.getDynamicModerationList(2);
//
//            List<ResultInfo> resultInfoList = changeToList(recognitionResult);
//
//            for (ResultInfo resultInfo: resultInfoList) {
//                if (Objects.isNull(resultInfo)) {
//                    continue;
//                }
//
//                AtomicBoolean needBlock = new AtomicBoolean(false);
//                moderationList.stream()
//                        .filter(moderation -> resultInfo.getLabel().equals(moderation.getLabel()) &&
//                                resultInfo.getScore() >= moderation.getScore())
//                        .findAny()
//                        .ifPresent(moderation -> needBlock.set(true));
//
//                if (needBlock.get()) {
//                    return CompletableFuture.completedFuture(0);
//                }
//            }
//        }
//
//        return CompletableFuture.completedFuture(1);
//    }
//
//    @Async
//    @Override
//    public CompletableFuture<Integer> dynamicVideoAuditAsync(Long dynamicId, String video) throws ApiException {
//        String url = cosService.getVideoAuditDomainName(ossConfProp.getUserDynamicCosBucket());
//        String objectPath = String.format("%s%s", ossConfProp.getCosUserDynamicRootPath(), video);
//        VideoAuditQueryResponse response = cosService.recognizeVideo(url, objectPath);
//
//        if (Objects.isNull(response.getJobsDetail().getSnapshot())) {
//            return CompletableFuture.completedFuture(1);
//        }
//
//        wrapperResult(response.getJobsDetail().getSnapshot());
//
//        List<Moderation> moderationList = moderationService.getDynamicModerationList(3);
//        List<ResultInfo> resultInfoList = changeToList(response.getJobsDetail().getSnapshot());
//
//        for (ResultInfo resultInfo: resultInfoList) {
//            if (Objects.isNull(resultInfo)) {
//                continue;
//            }
//
//            AtomicBoolean needBlock = new AtomicBoolean(false);
//            moderationList.stream()
//                    .filter(moderation -> resultInfo.getLabel().equals(moderation.getLabel()) &&
//                            resultInfo.getScore() >= moderation.getScore())
//                    .findAny()
//                    .ifPresent(moderation -> needBlock.set(true));
//
//            if (needBlock.get()) {
//                return CompletableFuture.completedFuture(0);
//            }
//        }
//
//        return CompletableFuture.completedFuture(1);
//    }
//
//    private void wrapperResult(RecognitionResult result) {
//        // 返回的label是null 自己填充下
//        if (result.getAdsInfo() != null) {
//            result.getAdsInfo().setLabel("Ad");
//        }
//        if (result.getPoliticsInfo() != null) {
//            result.getPoliticsInfo().setLabel("Polity");
//        }
//        if (result.getTerroristInfo() != null) {
//            result.getTerroristInfo().setLabel("Terror");
//        }
//        if (result.getPornInfo() != null) {
//            result.getPornInfo().setLabel("Porn");
//        }
//    }
//
//    private void wrapperResult(VideoAuditQueryResponse.Snapshot snapshot) {
//        // 返回的label是null 自己填充下
//        if (snapshot.getAdsInfo() != null) {
//            snapshot.getAdsInfo().setLabel("Ad");
//        }
//        if (snapshot.getPoliticsInfo() != null) {
//            snapshot.getPoliticsInfo().setLabel("Polity");
//        }
//        if (snapshot.getTerrorismInfo() != null) {
//            snapshot.getTerrorismInfo().setLabel("Terror");
//        }
//        if (snapshot.getPornInfo() != null) {
//            snapshot.getPornInfo().setLabel("Porn");
//        }
//    }
//
//    private List<ResultInfo> changeToList(RecognitionResult result) {
//        return Arrays.asList(result.getAdsInfo(),
//                result.getPornInfo(),
//                result.getTerroristInfo(),
//                result.getPoliticsInfo());
//    }
//
//    private List<ResultInfo> changeToList(VideoAuditQueryResponse.Snapshot snapshot) {
//        return Arrays.asList(snapshot.getAdsInfo(),
//                snapshot.getPornInfo(),
//                snapshot.getTerrorismInfo(),
//                snapshot.getPoliticsInfo());
//    }
}
