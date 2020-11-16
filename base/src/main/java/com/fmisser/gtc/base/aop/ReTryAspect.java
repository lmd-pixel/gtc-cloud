package com.fmisser.gtc.base.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class ReTryAspect {

    @Pointcut("@annotation(com.fmisser.gtc.base.aop.ReTry)")
    public void reTryEntryPoint(){

    }

    @Around("reTryEntryPoint()")
    public Object aroundReTry(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        Object target = joinPoint.getTarget();
        Method method = target.getClass().getMethod(signature.getName(),
                ((MethodSignature) signature).getParameterTypes());
        ReTry reTry = method.getAnnotation(ReTry.class);

        int maxTimes = reTry.times();
        int currTimes = 0;
        Class<? extends Throwable>[] classes = reTry.value();

        Exception lastException;
        // 是否需要重试，当返回的异常不是需要重试的异常时则不需要重试
        boolean needRetry;

        do {
            currTimes++;
            needRetry = false;
            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                lastException = e;

                // 查看是否为需要重试的异常
                for (Class<? extends Throwable> t : classes) {
                    if (t.isInstance(e)) {
                        needRetry = true;
                        break;
                    }
                }

                if (!needRetry) {
                    throw lastException;
                }
            }

        } while (currTimes <= maxTimes );

        throw lastException;
    }
}
