package com.fmisser.gtc.social.controller;

import com.fmisser.gtc.base.dto.cos.CosAuditDto;
import com.fmisser.gtc.base.response.ApiResp;
import com.fmisser.gtc.social.domain.Dynamic;
import com.fmisser.gtc.social.domain.Moderation;
import com.fmisser.gtc.social.repository.DynamicRepository;
import com.fmisser.gtc.social.service.AsyncService;
import com.fmisser.gtc.social.service.DynamicService;
import com.fmisser.gtc.social.service.ModerationService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author by fmisser
 * @create 2021/7/16 2:25 下午
 * @description TODO
 */
@Api(description = "Tencent Cos Callback")
@RestController
@RequestMapping("/cos_cb")
@AllArgsConstructor
@Slf4j
public class CosCallbackController {
    private final DynamicRepository dynamicRepository;
    private final ModerationService moderationService;
    private final AsyncService asyncService;

    @PostMapping("/audit_result")
    public ApiResp<String> auditResult(@RequestBody(required = false) CosAuditDto cosAuditDto) {
        if (Objects.isNull(cosAuditDto)) {
            log.error("cos audit callback request body is null.");
            return ApiResp.failed(-1, "cos audit callback request body is null.");
        }

        log.info("cos audit callback: {}.", cosAuditDto.toString());

        if (Objects.isNull(cosAuditDto.getData())) {
            log.error("cos audit callback request data is null.");
            return ApiResp.failed(-2, "cos audit callback request data is null.");
        }

        if (cosAuditDto.getData().getResult() == 0) {
            log.info("cos audit callback result is ok");
            return ApiResp.succeed("success");
        }

        if (Objects.isNull(cosAuditDto.getData().getHeader()) ||
            Objects.isNull(cosAuditDto.getData().getHeader().getDynamicId())) {
            log.error("cos audit callback request header is null.");
            return ApiResp.failed(-2, "cos audit callback request header is null.");
        }

        Long dynamicId = Long.parseLong(cosAuditDto.getData().getHeader().getDynamicId());
        asyncService.setDynamicStatusAsync(dynamicId, 2000L);
//        Dynamic dynamic = dynamicRepository.getOne(dynamicId);
//        if (dynamic.getStatus() != 1) {
//            dynamic.setStatus(1);
//            dynamicRepository.save(dynamic);
//        }


//        List<Moderation> moderationList = null;
//        if (dynamic.getType() == 1 || dynamic.getType() == 11 || dynamic.getType() == 21) {
//            moderationList = moderationService.getDynamicModerationList(2);
//        } else if (dynamic.getType() == 2 || dynamic.getType() == 12 || dynamic.getType() == 22) {
//            moderationList = moderationService.getDynamicModerationList(3);
//        }
//
//        if (Objects.isNull(moderationList)) {
//            log.error("cos audit callback moderation list is null.");
//            return ApiResp.failed(-2, "cos audit callback moderation list is null.");
//        }
//
//        wrapperResult(cosAuditDto.getData());
//        List<CosAuditDto.CosAuditInfo> auditInfoList = changeToList(cosAuditDto.getData());
//
//        for (CosAuditDto.CosAuditInfo info: auditInfoList) {
//            if (Objects.isNull(info)) {
//                continue;
//            }
//
//            AtomicBoolean needBlock = new AtomicBoolean(false);
//            moderationList.stream()
//                    .filter(moderation -> info.getLabel().equals(moderation.getLabel()) &&
//                            info.getScore() >= moderation.getScore())
//                    .findAny()
//                    .ifPresent(moderation -> needBlock.set(true));
//
//            if (needBlock.get()) {
//                log.info("cos audit callback need block with dynamic id: {}", dynamicId);
//                if (dynamic.getStatus() != 1) {
//                    dynamic.setStatus(1);
//                    dynamicRepository.save(dynamic);
//                }
//                break;
//            }
//        }

        return ApiResp.succeed("success");
    }

    private void wrapperResult(CosAuditDto.CosAuditData result) {
        // 返回的label是null 自己填充下
        if (result.getAdsInfo() != null) {
            result.getAdsInfo().setLabel("Ad");
        }
        if (result.getPoliticsInfo() != null) {
            result.getPoliticsInfo().setLabel("Polity");
        }
        if (result.getTerroristInfo() != null) {
            result.getTerroristInfo().setLabel("Terror");
        }
        if (result.getPornInfo() != null) {
            result.getPornInfo().setLabel("Porn");
        }
    }

    private List<CosAuditDto.CosAuditInfo> changeToList(CosAuditDto.CosAuditData result) {
        return Arrays.asList(result.getAdsInfo(),
                result.getPornInfo(),
                result.getTerroristInfo(),
                result.getPoliticsInfo());
    }
}
