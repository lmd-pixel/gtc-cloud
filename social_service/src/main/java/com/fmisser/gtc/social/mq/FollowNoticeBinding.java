package com.fmisser.gtc.social.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 通知关注者通道
 * 发送消息
 */

public interface FollowNoticeBinding {
    @Output("follow-notice-channel")
    MessageChannel followNoticeChannel();
}
