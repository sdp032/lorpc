package com.jkys.phobos.spring.client.beans;

import com.jkys.phobos.spring.client.Handler.PhobosHandler;
import com.jkys.phobos.spring.client.Handler.impl.DefaultPhobosHandler;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * Created by zdj on 2016/6/30.
 */
public class PhobosFactoryBean<T> implements FactoryBean<T> {

    private Class<T> phobosInterface;

    private String serviceAppName;

    private PhobosHandler phobosHandler;

    public T getObject() throws Exception {
        PhobosProxy phobosProxy = new PhobosProxy(phobosHandler == null ? new DefaultPhobosHandler(serviceAppName) : phobosHandler);
        T t = (T) Proxy.newProxyInstance(phobosInterface.getClassLoader(), new Class[]{phobosInterface}, phobosProxy);
        return t;
    }

    public Class<?> getObjectType() {
        return phobosInterface;
    }

    public boolean isSingleton() {
        return false;
    }

    public Class<T> getPhobosInterface() {
        return phobosInterface;
    }

    public void setPhobosInterface(Class<T> phobosInterface) {
        this.phobosInterface = phobosInterface;
    }

    public PhobosHandler getPhobosHandler() {
        return phobosHandler;
    }

    public void setPhobosHandler(PhobosHandler phobosHandler) {
        this.phobosHandler = phobosHandler;
    }

    public String getServiceAppName() {
        return serviceAppName;
    }

    public void setServiceAppName(String serviceAppName) {
        this.serviceAppName = serviceAppName;
    }
}
