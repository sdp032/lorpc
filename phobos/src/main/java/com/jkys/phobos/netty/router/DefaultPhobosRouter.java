package com.jkys.phobos.netty.router;

import com.jkys.phobos.codec.SerializeHandle;
import com.jkys.phobos.codec.SerializeHandleFactory;
import com.jkys.phobos.constant.ErrorEnum;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import com.jkys.phobos.remote.protocol.Response;
import com.jkys.phobos.server.Provider;
import com.jkys.phobos.server.ServerContext;
import com.jkys.phobos.server.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zdj on 2016/7/14.
 */
public class DefaultPhobosRouter implements PhobosRouter {
    private final Logger logger = LoggerFactory.getLogger(DefaultPhobosRouter.class);
    private ServerContext context = ServerContext.getInstance();

    private static final String SERVER_INFO = "server-info";
    private static final String SERVER_INFO_VER = "V1";

    public PhobosResponse route(PhobosRequest request) throws Exception {
        SerializeHandle handle = SerializeHandleFactory.create(request.getHeader().getSerializationType());
        String serviceName = request.getRequest().getServiceName();
        String serviceVersion = request.getRequest().getServiceVersion();
        Provider provider = context.getProvider(serviceName, serviceVersion);
        if (provider == null) {
            PhobosResponse phobosResponse = new PhobosResponse(request.getHeader(), new Response());
            phobosResponse.getResponse().setSuccess(false);
            phobosResponse.getResponse().setErrCode(ErrorEnum.UNKNOWN_SERVICE.name());
            return phobosResponse;
        }
        return new PhobosResponse(request.getHeader(), provider.invoke(handle, request.getRequest()));
        //client初次连接 返回server信息
//        if (SERVER_INFO.equals(serviceName) && SERVER_INFO.equals(methodName) && SERVER_INFO_VER.equals(serviceVersion)) {
//            Map<String, Method> map = context.getMethodMap();
//            ServerInfo serverInfo = new ServerInfo();
//            Iterator<String> iterator = map.keySet().iterator();
//            while (iterator.hasNext()) {
//                serverInfo.getServiceList().add(context.getServerAppName() + "." + iterator.next());
//            }
//            phobosResponse.getResponse().setSuccess(true);
//            phobosResponse.getResponse().setData(handle.objectToBytes(serverInfo));
//            return phobosResponse;
//        }
    }
}
