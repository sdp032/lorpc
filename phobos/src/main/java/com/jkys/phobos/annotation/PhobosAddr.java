package com.jkys.phobos.annotation;

import java.lang.annotation.*;

/**
 * Created by zdj on 2016/7/1.
 *
 * 该注解定义服务的版本号、访问地址或xbus注册地址
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface PhobosAddr {

    /**
     *  服务地址 与xbusAddr必须有一项，都有取addr
     */
    String[] addr() default {};

    /**
     *  注册地址 与addr必须有一项，都有取addr
     */
    String[] xbusAddr() default {};
}