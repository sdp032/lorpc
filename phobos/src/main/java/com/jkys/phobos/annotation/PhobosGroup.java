package com.jkys.phobos.annotation;

import java.lang.annotation.*;

/**
 * Created by zdj on 2016/7/4.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface PhobosGroup {
    String value() default "";
}
