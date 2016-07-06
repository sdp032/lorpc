package com.jkys.phobos.spring.client.beans;

import com.jkys.phobos.spring.client.Handler.PhobosHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zdj on 2016/6/30.
 */
public class PhobosProxy implements InvocationHandler{

    private PhobosHandler phobosHandler;

    public PhobosProxy(PhobosHandler phobosHandler){
        this.phobosHandler = phobosHandler;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result = phobosHandler.execution(method,args);

        return result;
    }
}
