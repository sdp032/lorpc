package com.jkys.phobos.spring;

import com.jkys.phobos.spring.Handler.impl.DefaultPhobosHandler;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * Created by zdj on 2016/6/30.
 */
public class PhobosFactoryBean<T> implements FactoryBean<T>{

    private Class<T> phobosInterface;

    public void setPhobosInterface(Class<T> phobosInterface){
        this.phobosInterface = phobosInterface;
    }

    public T getObject() throws Exception {
        PhobosProxy phobosProxy = new PhobosProxy(new DefaultPhobosHandler());
        T t = (T)Proxy.newProxyInstance(phobosInterface.getClassLoader(),new Class[]{phobosInterface},phobosProxy);
        return t;
    }

    public Class<?> getObjectType() {
        return phobosInterface;
    }

    public boolean isSingleton() {
        return false;
    }
}
