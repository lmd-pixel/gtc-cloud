package com.fmisser.gtc.push.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
 * @create 2021/5/25 1:50 下午
 * @description TODO
 */

@Slf4j
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class WebsocketInterceptor implements HandshakeInterceptor  {

    private final String identifierName;
    private final String[] paramsName;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();


            if (!StringUtils.isEmpty(identifierName)) {
                String idVal = servletRequest.getParameter(identifierName);
                attributes.put(identifierName, idVal);
            }

            if (paramsName != null) {
                Arrays.stream(paramsName)
                        .filter(p -> !StringUtils.isEmpty(p))
                        .forEach(p -> {
                            String pVal = servletRequest.getParameter(p);
                            attributes.put(p, pVal);
                        });
            }

            return true;

        } else {

            log.error("[websocket] request is not servlet server http request.");
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (Objects.nonNull(exception)) {
            log.error("[websocket] after hand shake has a exception: {}", exception.getMessage());
        }
    }
}
