package com.jkys.phobos.spring.client.Handler.impl;

import com.jkys.phobos.annotation.PhobosAddr;
import com.jkys.phobos.annotation.PhobosVersion;
import com.jkys.phobos.spring.client.Handler.PhobosHandler;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by zdj on 2016/7/1.
 */
public class DefaultPhobosHandler implements PhobosHandler {

    public Object execution(Method method,Object[] args) {

        PhobosAddr phobosAddrAnnotation = method.getDeclaringClass().getAnnotation(PhobosAddr.class);
        if(phobosAddrAnnotation == null) throw new NullPointerException("Annotation PhobosAddr is null for " + method.getDeclaringClass().getName());

        PhobosVersion phobosVersionAnnotation = method.getAnnotation(PhobosVersion.class) == null ? method.getDeclaringClass().getAnnotation(PhobosVersion.class) : method.getAnnotation(PhobosVersion.class);

        String version = phobosVersionAnnotation == null ? "" : phobosVersionAnnotation.version();
        String[] addr = phobosAddrAnnotation.addr();
        String[] xbusAddr = phobosAddrAnnotation.xbusAddr();

        if((addr==null||addr.length==0)&&(xbusAddr==null||xbusAddr.length==0))
            throw new RuntimeException("Annotation attribute addr and xbusAddr must be one is not null for" + method.getDeclaringClass().getName());

        //TODO 连接netty服务端
        System.out.println();
        System.out.println("version is " + version);
        System.out.println("addr is " + Arrays.toString(addr));
        System.out.println("xbusAddr is " + Arrays.toString(xbusAddr));

        return null;
    }
}
