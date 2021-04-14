package com.fmisser.gtc.social.mq;

import com.fmisser.gtc.base.dto.wx.WebHookFactory;
import com.fmisser.gtc.social.domain.Call;
import com.fmisser.gtc.social.domain.User;
import com.fmisser.gtc.social.feign.WxWebHookFeign;
import com.fmisser.gtc.social.repository.CallRepository;
import com.fmisser.gtc.social.service.UserService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;

import java.text.SimpleDateFormat;
import java.util.Optional;

/**
 * 微信 web hook
 */

@EnableBinding(WxWebHookBinding.class)
public class WxWebHookSubscribe {

    @Autowired
    private UserService userService;

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private WxWebHookFeign wxWebHookFeign;

    @StreamListener(WxWebHookBinding.INPUT)
    public void processMessage(Message<String> message) throws Exception {
        Channel channel = (Channel) message.getHeaders().get(AmqpHeaders.CHANNEL);
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);

        String payload = message.getPayload();
        String[] result = payload.split(",");
        int type = Integer.parseInt(result[0]);
        Long callId = Long.parseLong(result[1]);
        Long time = Long.parseLong(result[2]);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        if (type == 1) {
            Optional<Call> call = callRepository.findById(callId);
            if (call.isPresent()) {
                User userFrom = userService.getUserById(call.get().getUserIdFrom());
                User userTo = userService.getUserById(call.get().getUserIdTo());

                String msg = String.format("%s 用户:%s(%s)呼叫主播%s(%s),呼叫时长%d,主播已拒绝接听",
                        simpleDateFormat.format(call.get().getCreatedTime()),
                        userFrom.getNick(), userFrom.getDigitId(),
                        userTo.getNick(), userTo.getDigitId(),
                        time);

                wxWebHookFeign.postMsg(WebHookFactory.buildTextMsg(msg));
            }
        } else if (type == 2) {
            Optional<Call> call = callRepository.findById(callId);
            if (call.isPresent()) {
                User userFrom = userService.getUserById(call.get().getUserIdFrom());
                User userTo = userService.getUserById(call.get().getUserIdTo());

                String msg = String.format("%s 用户:%s(%s)呼叫主播%s(%s),呼叫时长%d,主播未接听",
                        simpleDateFormat.format(call.get().getCreatedTime()),
                        userFrom.getNick(), userFrom.getDigitId(),
                        userTo.getNick(), userTo.getDigitId(),
                        time);

                wxWebHookFeign.postMsg(WebHookFactory.buildTextMsg(msg));
            }
        }

        channel.basicAck(deliveryTag, false);
    }
}
