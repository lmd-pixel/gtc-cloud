package com.fmisser.gtc.base.dto.im;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * im 消息工厂
 */

public class ImMsgFactory {
    static public ImSendMsgDto buildTextMsg(String fromAccount, String toAccount, String content, boolean syncMsg) {
        ImSendMsgDto imSendMsgDto = new ImSendMsgDto();
        imSendMsgDto.setSyncOtherMachine(syncMsg ? 1 : 2);
        imSendMsgDto.setTo_Account(toAccount);
        imSendMsgDto.setFrom_Account(fromAccount);
        // 缓存7天
        imSendMsgDto.setMsgLifeTime(604800);
        // 不生成回调信息
        imSendMsgDto.setForbidCallbackControl(Arrays
                .asList("ForbidBeforeSendMsgCallback", "ForbidAfterSendMsgCallback"));
        imSendMsgDto.setMsgRandom(Math.abs(new Random().nextInt()));

        ImMsgBody imMsgBody = new ImMsgBody();
        imMsgBody.setMsgType("TIMTextElem");
        ImMsgBody.ImMsgContent imMsgContent = new ImMsgBody.ImMsgContent();
        imMsgContent.setText(content);
        imMsgBody.setMsgContent(imMsgContent);

        imSendMsgDto.setMsgBody(Collections.singletonList(imMsgBody));

        // apns
        ImOfflinePushInfo imOfflinePushInfo = new ImOfflinePushInfo();
        imOfflinePushInfo.setPushFlag(0);
        imOfflinePushInfo.setTitle("");
        imOfflinePushInfo.setDesc(content);
        imOfflinePushInfo.setExt("");

        ImOfflinePushInfo.ImApnsInfo imApnsInfo = new ImOfflinePushInfo.ImApnsInfo();
        imApnsInfo.setSound("");
        imApnsInfo.setImage("");
        imApnsInfo.setBadgeMode(1);
        imApnsInfo.setTitle("");
        imApnsInfo.setSubTitle("");
        imOfflinePushInfo.setApnsInfo(imApnsInfo);

        ImOfflinePushInfo.ImAndroidInfo imAndroidInfo = new ImOfflinePushInfo.ImAndroidInfo();
        imAndroidInfo.setSound("");
        imOfflinePushInfo.setAndroidInfo(imAndroidInfo);

        imSendMsgDto.setOfflinePushInfo(imOfflinePushInfo);

        return imSendMsgDto;
    }

    static public ImSendMsgDto buildGiftMsg(String fromAccount, String toAccount, String content,
                                            int tag, Long giftId, int giftCount, String giftName,
                                            boolean syncMsg) {
        ImSendMsgDto imSendMsgDto = new ImSendMsgDto();
        imSendMsgDto.setSyncOtherMachine(syncMsg ? 1 : 2);
        imSendMsgDto.setTo_Account(toAccount);
        imSendMsgDto.setFrom_Account(fromAccount);
        // 缓存7天
        imSendMsgDto.setMsgLifeTime(604800);
        // 不生成回调信息
        imSendMsgDto.setForbidCallbackControl(Arrays
                .asList("ForbidBeforeSendMsgCallback", "ForbidAfterSendMsgCallback"));
        imSendMsgDto.setMsgRandom(Math.abs(new Random().nextInt()));

        // 文本消息
//        ImMsgBody imMsgBody = new ImMsgBody();
//        imMsgBody.setMsgType("TIMTextElem");
//        ImMsgBody.ImMsgContent imMsgContent = new ImMsgBody.ImMsgContent();
//        imMsgContent.setText(content);
//        imMsgBody.setMsgContent(imMsgContent);

        // 自定义消息
        ImMsgBody customMsgBody = new ImMsgBody();
        customMsgBody.setMsgType("TIMCustomElem");
        ImMsgBody.ImMsgContent customMsgContent = new ImMsgBody.ImMsgContent();

        String msgData = String.format("{\"tag\":%d,\"giftId\":%d,\"giftCount\":%d,\"giftName\":\"%s\"}",
                tag, giftId, giftCount, giftName);
        customMsgContent.setData(msgData);

        customMsgContent.setDesc("这是一个礼物");
        customMsgBody.setMsgContent(customMsgContent);

//        imSendMsgDto.setMsgBody(Arrays.asList(imMsgBody, customMsgBody));
        imSendMsgDto.setMsgBody(Collections.singletonList(customMsgBody));

        // apns
        ImOfflinePushInfo imOfflinePushInfo = new ImOfflinePushInfo();
        imOfflinePushInfo.setPushFlag(0);
        imOfflinePushInfo.setTitle("");
        imOfflinePushInfo.setDesc(content);
        imOfflinePushInfo.setExt("");

        ImOfflinePushInfo.ImApnsInfo imApnsInfo = new ImOfflinePushInfo.ImApnsInfo();
        imApnsInfo.setSound("");
        imApnsInfo.setImage("");
        imApnsInfo.setBadgeMode(1);
        imApnsInfo.setTitle("");
        imApnsInfo.setSubTitle("");
        imOfflinePushInfo.setApnsInfo(imApnsInfo);

        ImOfflinePushInfo.ImAndroidInfo imAndroidInfo = new ImOfflinePushInfo.ImAndroidInfo();
        imAndroidInfo.setSound("");
        imOfflinePushInfo.setAndroidInfo(imAndroidInfo);

        imSendMsgDto.setOfflinePushInfo(imOfflinePushInfo);

        return imSendMsgDto;
    }

    static public ImSendMsgDto buildChargingMsg(String fromAccount, String toAccount, String content, int tag, int coin, int card, boolean syncMsg) {
        ImSendMsgDto imSendMsgDto = new ImSendMsgDto();
        imSendMsgDto.setSyncOtherMachine(syncMsg ? 1 : 2);
        imSendMsgDto.setTo_Account(toAccount);
        imSendMsgDto.setFrom_Account(fromAccount);
        // 缓存7天
        imSendMsgDto.setMsgLifeTime(604800);
        // 不生成回调信息
        imSendMsgDto.setForbidCallbackControl(Arrays
                .asList("ForbidBeforeSendMsgCallback", "ForbidAfterSendMsgCallback"));
        imSendMsgDto.setMsgRandom(Math.abs(new Random().nextInt()));

        // 文本消息
//        ImMsgBody imMsgBody = new ImMsgBody();
//        imMsgBody.setMsgType("TIMTextElem");
//        ImMsgBody.ImMsgContent imMsgContent = new ImMsgBody.ImMsgContent();
//        imMsgContent.setText(content);
//        imMsgBody.setMsgContent(imMsgContent);

        // 自定义消息
        ImMsgBody customMsgBody = new ImMsgBody();
        customMsgBody.setMsgType("TIMCustomElem");
        ImMsgBody.ImMsgContent customMsgContent = new ImMsgBody.ImMsgContent();

        String msgData = String.format("{\"tag\":%d,\"coin\":%d,\"card\":%d}", tag, coin, card);
        customMsgContent.setData(msgData);

        customMsgContent.setDesc(content);
        customMsgBody.setMsgContent(customMsgContent);

//        imSendMsgDto.setMsgBody(Arrays.asList(imMsgBody, customMsgBody));
        imSendMsgDto.setMsgBody(Collections.singletonList(customMsgBody));

        // apns
        ImOfflinePushInfo imOfflinePushInfo = new ImOfflinePushInfo();
        imOfflinePushInfo.setPushFlag(1);
        imOfflinePushInfo.setTitle("");
        imOfflinePushInfo.setDesc(content);
        imOfflinePushInfo.setExt("");

        ImOfflinePushInfo.ImApnsInfo imApnsInfo = new ImOfflinePushInfo.ImApnsInfo();
        imApnsInfo.setSound("");
        imApnsInfo.setImage("");
        imApnsInfo.setBadgeMode(1);
        imApnsInfo.setTitle("");
        imApnsInfo.setSubTitle("");
        imOfflinePushInfo.setApnsInfo(imApnsInfo);

        ImOfflinePushInfo.ImAndroidInfo imAndroidInfo = new ImOfflinePushInfo.ImAndroidInfo();
        imAndroidInfo.setSound("");
        imOfflinePushInfo.setAndroidInfo(imAndroidInfo);

        imSendMsgDto.setOfflinePushInfo(imOfflinePushInfo);

        return imSendMsgDto;
    }
}
