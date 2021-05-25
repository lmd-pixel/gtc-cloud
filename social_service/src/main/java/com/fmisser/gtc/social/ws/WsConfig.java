package com.fmisser.gtc.social.ws;

import com.fmisser.fpp.push.websocket.conf.WsMsgBinding;
import com.fmisser.fpp.push.websocket.conf.WsMsgEndpoint;

/**
 * @author by fmisser
 * @create 2021/5/24 9:09 下午
 * @description TODO
 */

@WsMsgBinding
public class WsConfig {
    @WsMsgEndpoint(value = "comm", id = "digit_id", params = {"p1", "p2"})
    String endpoint() {
        return "";
    }
}
