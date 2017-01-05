package com.jkys.phobos.netty.router;

import com.jkys.phobos.codec.SerializeHandle;
import com.jkys.phobos.codec.SerializeHandleFactory;
import com.jkys.phobos.constant.ErrorEnum;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import com.jkys.phobos.remote.protocol.Response;
import com.jkys.phobos.server.PhobosContext;
import com.jkys.phobos.server.ServerInfo;
import org.msgpack.type.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zdj on 2016/7/14.
 */
public class DefaultPhobosRouter implements PhobosRouter {

    private final Logger logger = LoggerFactory.getLogger(DefaultPhobosRouter.class);

    private static final String SERVER_INFO = "server-info";
    private static final String SERVER_INFO_VER = "V1";

    public PhobosResponse route(PhobosRequest request) throws Exception {

        PhobosContext context = PhobosContext.getInstance();
        PhobosResponse phobosResponse = new PhobosResponse(request.getHeader(), new Response());

        String serviceName = request.getRequest().getServiceName();
        String serviceVersion = request.getRequest().getServiceVersion();
        String methodName = request.getRequest().getMethodName();
        String group = request.getRequest().getGroup();

        SerializeHandle handle = SerializeHandleFactory.create(request.getHeader().getSerializationType());

        //logger.info("{}.{}.{}.{}", serviceName, methodName, serviceVersion, group);
        logger.info("{}.{}.{}", serviceName, methodName, serviceVersion);

        //client初次连接 返回server信息
        if (SERVER_INFO.equals(serviceName) && SERVER_INFO.equals(methodName) && SERVER_INFO_VER.equals(serviceVersion)) {
            Map<String, Method> map = context.getMethodMap();
            ServerInfo serverInfo = new ServerInfo();
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                serverInfo.getServiceList().add(context.getServerAppName() + "." + iterator.next());
            }
            phobosResponse.getResponse().setSuccess(true);
            phobosResponse.getResponse().setData(handle.objectToBytes(serverInfo));
            return phobosResponse;
        }

        //调用client端请求的服务
        //Method method = PhobosContext.getInstance().getMethod(serviceName + "." + methodName + "."  + group + "." + serviceVersion);
        Method method = PhobosContext.getInstance().getMethod(serviceName + "." + methodName + "." + serviceVersion);
        if (method == null) {
            phobosResponse.getResponse().setErrCode(ErrorEnum.UNKNOWN_METHOD.name());
            phobosResponse.getResponse().setSuccess(false);
            return phobosResponse;
        }

        byte[] paramsBytes = (byte[]) request.getRequest().getObject().get(0);
        Class<?>[] paramsType = method.getParameterTypes();
        Type[] genericParamsType = method.getGenericParameterTypes();

        Object[] params = null;
        try {
            params = handle.bytesToParams(paramsBytes, paramsType, genericParamsType);
        } catch (PhobosException e){
            phobosResponse.getResponse().setErrCode(e.code);
            phobosResponse.getResponse().setErrMessage(e.message);
            phobosResponse.getResponse().setSuccess(false);
            return phobosResponse;
        }

        //Object service = method.getDeclaringClass().newInstance();
        Object service = PhobosContext.getInstance().getService(serviceName, group, serviceVersion).getService();

        Object value = null;
        try {
            value = method.invoke(service, params);
        } catch (Exception e) {
            e.printStackTrace();
            phobosResponse.getResponse().setErrCode(ErrorEnum.SYSTEM_ERROR.name());
            phobosResponse.getResponse().setSuccess(false);
            phobosResponse.getResponse().setErrMessage("调用服务时出现异常");
            return phobosResponse;
        }
        phobosResponse.getResponse().setData(handle.objectToBytes(value));
        phobosResponse.getResponse().setSuccess(true);
        return phobosResponse;
    }
}
