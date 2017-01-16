package com.jkys.phobos.server;

import com.github.infrmods.xbus.item.ServiceDesc;
import com.jkys.phobos.annotation.ServiceUtil;
import com.jkys.phobos.serialization.SerializationType;
import com.jkys.phobos.constant.ErrorEnum;
import com.jkys.phobos.proto.Function;
import com.jkys.phobos.proto.ServiceProto;
import com.jkys.phobos.protocol.Request;
import com.jkys.phobos.protocol.Response;
import com.jkys.phobos.serialization.SerializerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by lo on 1/6/17.
 */
public class Provider {
    private SerializerFactory serializerFactory = new SerializerFactory();
    private Class<?> serviceInterface;
    private Class<?> implClass;
    private ServiceProto serviceProto;
    private ServiceDesc serviceDesc;
    private Object impl = null;

    public Provider(Class<?> implClass) {
        this.implClass = implClass;
        resolveService();
    }

    Provider(Object impl) {
        this(impl.getClass());
        setImpl(impl);
    }

    public void setImpl(Object impl) {
        this.impl = impl;
    }

    private void resolveService() {
        Class<?>[] interfaces = this.implClass.getInterfaces();
        if (interfaces == null || interfaces.length != 1) {
            // FIXME exception
            throw new RuntimeException("invalid service impl");
        }
        serviceInterface = interfaces[0];
        serviceProto = new ServiceProto(serviceInterface);

        try {
            serviceDesc = new ServiceDesc(
                    serviceProto.getServiceName(),
                    serviceProto.getServiceVersion(), "service-kit",
                    serviceProto.toYaml());
        } catch (Exception e) {
            // FIXME
            throw new RuntimeException(e);
        }
    }

    public Response invoke(SerializationType serializationType, Request request) {
        Response response = new Response();
        response.setSuccess(false);
        Function function = serviceProto.getFunctions().get(request.getMethodName());
        if (function == null) {
            response.setErrCode(ErrorEnum.UNKNOWN_METHOD.name());
            return response;
        }

        // TODO serialization exception
        Object[] params = serializerFactory.get(serializationType, function.getParamsType()).decodeArray(request.getData());
        Object result;
        try {
            result = function.getMethod().invoke(impl, params);
        } catch (IllegalAccessException e) {
            // FIXME
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            // TODO
            response.setErrCode(ErrorEnum.SYSTEM_ERROR.name());
            response.setErrMessage("调用服务时出现异常");
            return response;
        }
        response.setData(serializerFactory.get(serializationType, function.getReturnType()).encode(result));
        response.setSuccess(true);
        return response;
    }

    public String getName() {
        return serviceProto.getServiceName();
    }

    public String getVersion() {
        return serviceProto.getServiceVersion();
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public ServiceDesc getServiceDesc() {
        return serviceDesc;
    }

    public Class<?> getImplClass() {
        return implClass;
    }

    public Object getImpl() {
        return impl;
    }

    public String key() {
        return ServiceUtil.serviceKey(serviceProto.getServiceName(), serviceProto.getServiceVersion());
    }
}
