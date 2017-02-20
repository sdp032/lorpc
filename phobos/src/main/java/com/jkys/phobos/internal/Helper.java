package com.jkys.phobos.internal;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;

/**
 * Created by lo on 1/20/17.
 */
public class Helper {
    private static final ClassLoader internalClassLoader = new InternalClassLoader();
    private static final String internalImplName = PhobosInternal.class.getName() + "Impl";
    private static PhobosInternal phobosInternal;
    static {
        Class<?> phobosInternalClass;
        try {
            phobosInternalClass  = internalClassLoader.loadClass(internalImplName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("load phobos internal fail", e);
        }
        try {
            phobosInternal = (PhobosInternal) phobosInternalClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("create phobos internal fail", e);
        }
    }

    public static InvocationHandler newClientProxy(Class<?> interfaceClass, String name, String version) {
        return phobosInternal.newClientProxy(interfaceClass, name, version);
    }

    public static void registryProvider(Class<?> implClass) {
        phobosInternal.registryProvider(implClass);
    }

    public static void triggerServer(ApplicationContext appCtx) {
        phobosInternal.triggerServer(appCtx);
    }

    public static void joinServer() throws InterruptedException {
        phobosInternal.joinServer();
    }

    public static void stopServer(Long forceStopTimeoutInSeconds) {
        phobosInternal.stopServer(forceStopTimeoutInSeconds);
    }
}
