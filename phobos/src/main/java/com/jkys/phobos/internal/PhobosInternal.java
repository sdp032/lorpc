package com.jkys.phobos.internal;

import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;

/**
 * Created by lo on 1/19/17.
 */
public interface PhobosInternal {
    InvocationHandler newClientProxy(Class<?> interfaceClass, String name, String version);
    void registryProvider(String bean);
    void registryProvider(Object impl);
    void triggerServer(ApplicationContext appCtx);
    void joinServer() throws InterruptedException;
    void stopServer(Long forceStopTimeoutInSeconds);
}
