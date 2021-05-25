package com.fmisser.fpp.push.websocket.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author by fmisser
 * @create 2021/5/24 6:05 下午
 * @description TODO
 */
@Aspect
@Component
public class WsMsgHandlerAspect {

    @Pointcut("execution(* com.fmisser.fpp.push.websocket.conf.WebsocketHandler.handleTextMessage(..))")
    public void handleTextMessage() {

    }

    @Pointcut("execution(* com.fmisser.fpp.push.websocket.conf.WebsocketHandler.handleBinaryMessage(..))")
    public void handleBinaryMessage() {

    }

    @Pointcut("execution(* com.fmisser.fpp.push.websocket.conf.WebsocketHandler.handlePongMessage(..))")
    public void handlePongMessage() {

    }

//    @Around("handleTextMessage()")
//    public Object aroundHandlerTextMessage(ProceedingJoinPoint joinPoint) throws Throwable {
//        return joinPoint.proceed();
//    }

    @After("handleTextMessage()")
    public void afterHandlerTextMessage(JoinPoint joinPoint) throws Throwable {

    }
}
