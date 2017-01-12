package com.jkys.phobos.spring.client.beans;

import com.jkys.phobos.annotation.Rename;
import com.jkys.phobos.client.ClientContext;
import com.jkys.phobos.codec.SerializeHandle;
import com.jkys.phobos.codec.SerializeHandleFactory;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.protocol.Header;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;
import com.jkys.phobos.protocol.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zdj on 2016/6/30.
 */
public class PhobosProxy implements InvocationHandler {
    private static Logger logger = LoggerFactory.getLogger(PhobosProxy.class);

    private Class<?> interfaceClass;
    private String serviceName;
    private String serviceVersion;

    public PhobosProxy(Class<?> interfaceClass, String name, String version) {
        this.interfaceClass = interfaceClass;
        this.serviceName = name;
        this.serviceVersion = version;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> params = new ArrayList<>(args.length);
        Collections.addAll(params, args);

        String methodName = method.getName();
        Rename rename = method.getAnnotation(Rename.class);
        if (rename != null && !rename.value().equals("")) {
            methodName = rename.value();
        }

        PhobosConfig phobosConfig = PhobosConfig.getInstance();
        PhobosRequest request = new PhobosRequest(new Header(), new Request());
        request.getHeader().setSerializationType(phobosConfig.getClient().getSerializationType().getType());
        request.getRequest().setClientAppName(phobosConfig.getRegistry().getAppName());
        request.getRequest().setServiceName(serviceName);
        request.getRequest().setMethodName(methodName);
        request.getRequest().setServiceVersion(serviceVersion);
        request.getRequest().setTraceId(new byte[16]); //TODO 规则待定
        request.getRequest().setObject(params);

        long timeout = phobosConfig.getClient().getRequestTimeout();
        PhobosResponse response = ClientContext.getInstance().request(request, timeout, TimeUnit.SECONDS);
        if(!response.getResponse().isSuccess()){
            logger.error("{}:{} : {}" , serviceName, serviceVersion, methodName);
            logger.error("err code: {}, err message: {}", response.getResponse().getErrCode(), response.getResponse().getErrMessage());
            throw new PhobosException(response.getResponse().getErrCode(), response.getResponse().getErrMessage());
        }
        SerializeHandle handle = SerializeHandleFactory.create(request.getHeader().getSerializationType());
        return handle.bytesToReturnVal(response.getResponse().getData(),
                method.getReturnType(), method.getGenericReturnType());
    }
}
