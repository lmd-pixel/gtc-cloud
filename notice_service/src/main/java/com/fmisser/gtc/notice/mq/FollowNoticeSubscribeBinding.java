package com.fmisser.gtc.notice.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * 通知关注者通道
 * 接收消息
 */
public interface FollowNoticeSubscribeBinding {
    @Input("follow-notice-channel")
    SubscribableChannel followNoticeChannel();
}
