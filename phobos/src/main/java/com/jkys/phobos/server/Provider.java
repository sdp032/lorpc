package com.jkys.phobos.server;

import com.github.infrmods.xbus.item.ServiceDesc;
import com.jkys.phobos.annotation.PhobosGroup;
import com.jkys.phobos.annotation.PhobosVersion;
import com.jkys.phobos.annotation.Rename;
import com.jkys.phobos.codec.SerializeHandle;
import com.jkys.phobos.constant.ErrorEnum;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.remote.protocol.Request;
import com.jkys.phobos.remote.protocol.Response;
import com.jkys.phobos.util.yaml.BeanRepresenter;
import com.jkys.phobos.util.yaml.PhobosRepresentr;
import com.jkys.phobos.util.yaml.Yaml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lo on 1/6/17.
 */
public class Provider {
    private String name;
    private String version;
    private Class<?> serviceInterface;
    private Class<?> implClass;
    private ServiceDesc serviceDesc;
    private Object impl = null;
    private ConcurrentHashMap<String, Method> methods;

    public Provider(Class<?> implClass) {
        this.implClass = implClass;
        resolveService();
    }

    Provider(Object impl) {
        this(impl.getClass());
        setImpl(impl);
    }

    private void resolveService() {
        Class<?>[] interfaces = this.implClass.getInterfaces();
        if (interfaces == null || interfaces.length != 1) {
            // FIXME exception
            throw new RuntimeException("invalid service impl");
        }
        serviceInterface = interfaces[0];

        PhobosGroup name = serviceInterface.getAnnotation(PhobosGroup.class);
        if (name.value().equals("")) {
            // FIXME exception
            throw new RuntimeException("invalid service name");
        }
        this.name = name.value();

        PhobosVersion version = serviceInterface.getAnnotation(PhobosVersion.class);
        if (version.version().equals("")) {
            // FIXME exception
            throw new RuntimeException("invalid version");
        }
        this.version = version.version();

        Yaml yaml = new Yaml(new PhobosRepresentr(new BeanRepresenter()));
        try {
            serviceDesc = new ServiceDesc(this.name, this.version, "service-kit",
                    yaml.dump(serviceInterface));
        } catch (Exception e) {
            // FIXME
            throw new RuntimeException(e);
        }
    }

    public void setImpl(Object impl) {
        if (!impl.getClass().equals(implClass)) {
            throw new RuntimeException("invalid impl");
        }
        this.impl = impl;
        methods = new ConcurrentHashMap<>();
        for (Method method : serviceInterface.getMethods()) {
            String name = method.getName();
            Rename rename = method.getAnnotation(Rename.class);
            if (rename != null && !rename.value().equals("")) {
                name = rename.value();
            }
            if (methods.putIfAbsent(name, method) != null) {
                // FIXME exception
                throw new RuntimeException("duplicated method: " + name);
            }
        }
    }

    public Response invoke(SerializeHandle handle, Request request) {
        Response response = new Response();
        response.setSuccess(false);
        Method method = methods.get(request.getMethodName());
        if (method == null) {
            response.setErrCode(ErrorEnum.UNKNOWN_METHOD.name());
            return response;
        }

        byte[] paramsBytes = (byte[]) request.getObject().get(0);
        Class<?>[] paramsType = method.getParameterTypes();
        Type[] genericParamsType = method.getGenericParameterTypes();

        Object[] params = null;
        try {
            params = handle.bytesToParams(paramsBytes, paramsType, genericParamsType);
        } catch (PhobosException e){
            response.setErrCode(e.code);
            response.setErrMessage(e.message);
            return response;
        } catch (Exception e) {
            // FIXME
            throw new RuntimeException(e);
        }

        Object result;
        try {
            result = method.invoke(impl, params);
        } catch (IllegalAccessException e) {
            // FIXME
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            response.setErrCode(ErrorEnum.SYSTEM_ERROR.name());
            response.setErrMessage("调用服务时出现异常");
            return response;
        }
        try {
            response.setData(handle.objectToBytes(result));
        } catch (Exception e) {
            // FIXME
            throw new RuntimeException(e);
        }
        response.setSuccess(true);
        return response;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
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
        return name + ":" + version;
    }
}
