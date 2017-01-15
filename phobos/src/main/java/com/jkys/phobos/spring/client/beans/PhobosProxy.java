package com.jkys.phobos.spring.client.beans;

import com.jkys.phobos.client.ClientContext;
import com.jkys.phobos.serialization.SerializationType;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.proto.Function;
import com.jkys.phobos.proto.ServiceProto;
import com.jkys.phobos.protocol.Header;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;
import com.jkys.phobos.protocol.Request;
import com.jkys.phobos.serialization.SerializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by zdj on 2016/6/30.
 */
public class PhobosProxy implements InvocationHandler {
    private static Logger logger = LoggerFactory.getLogger(PhobosProxy.class);
    private static SerializerFactory serializerFactory = new SerializerFactory();

    private ConcurrentHashMap<Method, Function> functions = new ConcurrentHashMap<>();
    private String serviceName;
    private String serviceVersion;

    public PhobosProxy(Class<?> interfaceClass, String name, String version) {
        this.serviceName = name;
        this.serviceVersion = version;
        ServiceProto serviceProto = new ServiceProto(interfaceClass);
        for (Function function : serviceProto.getFunctions().values()) {
            functions.put(function.getMethod(), function);
        }
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO assert function is not null
        Function function = functions.get(method);
        PhobosConfig phobosConfig = PhobosConfig.getInstance();
        PhobosRequest request = new PhobosRequest(new Header(), new Request());
        SerializationType serializationType = phobosConfig.getClient().getSerializationType();
        request.getHeader().setSerializationType(serializationType.getType());
        request.getRequest().setClientAppName(phobosConfig.getRegistry().getAppName());
        request.getRequest().setServiceName(serviceName);
        request.getRequest().setMethodName(function.getName());
        request.getRequest().setServiceVersion(serviceVersion);
        request.getRequest().setTraceId(new byte[16]); //TODO 规则待定
        request.getRequest().setData(serializerFactory.get(serializationType, function.getParamsType()).encode(args));

        long timeout = phobosConfig.getClient().getRequestTimeout();
        PhobosResponse response = ClientContext.getInstance().request(request, timeout, TimeUnit.SECONDS);
        if(!response.getResponse().isSuccess()){
            logger.error("{}:{} : {}" , serviceName, serviceVersion, function.getName());
            logger.error("err code: {}, err message: {}", response.getResponse().getErrCode(), response.getResponse().getErrMessage());
            throw new PhobosException(response.getResponse().getErrCode(), response.getResponse().getErrMessage());
        }

        return serializerFactory.get(serializationType, function.getReturnType()
            ).decode(response.getResponse().getData());
    }
}
