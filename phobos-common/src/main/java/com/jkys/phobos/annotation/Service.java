package com.jkys.phobos.annotation;

import java.lang.annotation.*;

/**
 * Created by zdj on 2016/7/1.
 * <p>
 * 服务注解 "name:version"
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
