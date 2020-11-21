package com.fmisser.gtc.base.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 重试机制
 * @deprecated 官方已有实现 @Retryable
 */

@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReTry {
    int times() default 3;

    Class<? extends Throwable>[] value() default {};
}
