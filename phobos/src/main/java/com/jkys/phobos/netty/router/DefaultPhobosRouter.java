package com.jkys.phobos.netty.router;

import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import com.jkys.phobos.remote.protocol.Response;
import com.jkys.phobos.server.PhobosContext;
import com.jkys.phobos.server.ServerInfo;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zdj on 2016/7/14.
 */
public class DefaultPhobosRouter implements PhobosRouter {

    private static final String SERVER_INFO = "server-info";
    private static final String SERVER_INFO_VER = "V1";

    public PhobosResponse route(PhobosRequest request) throws Exception{

        PhobosContext context = PhobosContext.getInstance();
        PhobosResponse phobosResponse = new PhobosResponse(request.getHeader(),new Response());

        String serviceName = request.getRequest().getServiceName();
        String serviceVersion = request.getRequest().getServiceVersion();
        String methodName = request.getRequest().getMethodName();

        //client初次连接 返回server信息
        if(SERVER_INFO.equals(serviceName)&&SERVER_INFO.equals(methodName)&&SERVER_INFO_VER.equals(serviceVersion)){
            Map<String,Method> map = context.getMethodMap();
            ServerInfo serverInfo = new ServerInfo();
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()){
                serverInfo.getServiceList().add(iterator.next());
            }
            if(phobosResponse.getHeader().getSerializationType() == Header.SerializationType.MAGPACK.serializationType){
                phobosResponse.getResponse().setData(MsgpackUtil.MESSAGE_PACK.write(serverInfo));
            }


            return phobosResponse;
        }

        // TODO 调用client端请求的服务
        return phobosResponse;
    }
}
