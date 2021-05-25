package com.fmisser.fpp.push.websocket.conf;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @author by fmisser
 * @create 2021/5/24 3:39 下午
 * @description TODO
 */

@Slf4j
@AllArgsConstructor
public class WebsocketInterceptor implements HandshakeInterceptor {
    private final String id;
    private final boolean auth;
    private final String authName;
    private final String[] params;

    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest,
                                   ServerHttpResponse serverHttpResponse,
                                   WebSocketHandler webSocketHandler,
                                   Map<String, Object> map) throws Exception {
        if (serverHttpRequest instanceof ServletServerHttpRequest) {
            HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();

            // TODO: 2021/5/25 auth 操作
            if (auth) {
                String authVal = request.getParameter(authName);
                log.error("[websocket] hand shake not support auth with value: {}.", authVal);
                throw new UnsupportedOperationException("暂不支持鉴权操作");
            }

            if (!StringUtils.isEmpty(id)) {
                String idVal = request.getParameter(id);
                map.put(id, idVal);
            }

            if (params != null) {
                Arrays.stream(params)
                        .filter(p -> !StringUtils.isEmpty(p))
                        .forEach(p -> {
                            String pVal = request.getParameter(p);
                            map.put(p, pVal);
                        });
            }

            return true;
        } else {
            log.error("[websocket] request is not servlet server http request.");
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest,
                               ServerHttpResponse serverHttpResponse,
                               WebSocketHandler webSocketHandler,
                               Exception e) {
        if (Objects.nonNull(e)) {
            log.error("[websocket] after hand shake has a exception: {}", e.getMessage());
        }
    }
}
