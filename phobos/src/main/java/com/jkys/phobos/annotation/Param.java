package com.jkys.phobos.annotation;

import java.lang.annotation.*;

/**
 * Created by zdj on 2016/11/22.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Documented
public @interface Param {
    String name();
}
