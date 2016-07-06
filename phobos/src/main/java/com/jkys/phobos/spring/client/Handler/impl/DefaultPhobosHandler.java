package com.jkys.phobos.spring.client.Handler.impl;

import com.jkys.phobos.annotation.PhobosGroup;
import com.jkys.phobos.annotation.PhobosVersion;
import com.jkys.phobos.spring.client.Handler.PhobosHandler;

import java.lang.reflect.Method;

/**
 * Created by zdj on 2016/7/1.
 */
public class DefaultPhobosHandler implements PhobosHandler {

    public Object execution(Method method,Object[] args) {

        PhobosGroup phobosGroupAnnotation = method.getAnnotation(PhobosGroup.class) == null ?
                method.getDeclaringClass().getAnnotation(PhobosGroup.class) :
                method.getAnnotation(PhobosGroup.class);
        if(phobosGroupAnnotation == null)
            throw new NullPointerException("PhobosGroup is null for " + method.getDeclaringClass().getName());

        PhobosVersion phobosVersionAnnotation = method.getAnnotation(PhobosVersion.class) == null ?
                method.getDeclaringClass().getAnnotation(PhobosVersion.class) :
                method.getAnnotation(PhobosVersion.class);
        if(phobosVersionAnnotation == null)
            throw new NullPointerException("PhobosVersion is null for " + method.getDeclaringClass().getName());

        String version = phobosVersionAnnotation.version();
        String group = phobosGroupAnnotation.value();

        //TODO 连接netty服务端
        System.out.println();
        System.out.println("version is " + version);

        return null;
    }
}
