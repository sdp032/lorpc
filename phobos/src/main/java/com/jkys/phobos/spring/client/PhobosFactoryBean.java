package com.jkys.phobos.spring.client;

import com.jkys.phobos.internal.Helper;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by zdj on 2016/6/30.
 */
public class PhobosFactoryBean<T> implements FactoryBean<T> {

    private Class<T> serviceInterface;
    private String serviceName;
    private String serviceVersion;

    public T getObject() throws Exception {
        InvocationHandler phobosProxy = Helper.newClientProxy(serviceInterface, serviceName, serviceVersion);
        T t = (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, phobosProxy);
        return t;
    }

    public Class<?> getObjectType() {
        return serviceInterface;
    }

    public boolean isSingleton() {
        return false;
    }

    public Class<T> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }
}
