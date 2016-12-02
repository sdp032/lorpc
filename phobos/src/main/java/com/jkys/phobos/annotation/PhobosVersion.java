package com.jkys.phobos.annotation;

import java.lang.annotation.*;

/**
 * Created by zdj on 2016/7/1.
 * <p>
 * 服务版本注解  可用于接口或方法 同时存在时以方法为准
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PhobosVersion {

    String version() default "";
}
