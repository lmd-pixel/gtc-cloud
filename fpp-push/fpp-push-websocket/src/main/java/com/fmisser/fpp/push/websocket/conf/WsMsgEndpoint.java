package com.fmisser.fpp.push.websocket.conf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author by fmisser
 * @create 2021/5/24 6:38 下午
 * @description TODO
 */

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE,
        ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface WsMsgEndpoint {
    String value() default "";

    String id() default "";

    boolean auth() default false;

    String authName() default "token";

    String[] params() default {};

    boolean sockJs() default false;
}
