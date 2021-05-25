package com.fmisser.fpp.push.websocket.conf;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author by fmisser
 * @create 2021/5/24 6:42 下午
 * @description TODO
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Configuration
public @interface WsMsgBinding {
    Class<?>[] value() default {};
}
