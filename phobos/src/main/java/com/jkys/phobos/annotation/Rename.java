package com.jkys.phobos.annotation;

import java.lang.annotation.*;

/**
 * Created by lo on 1/6/17.
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Rename {
    String value() default "";
}
