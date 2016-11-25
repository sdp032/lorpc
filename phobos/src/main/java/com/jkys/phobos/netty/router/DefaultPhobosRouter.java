package com.jkys.phobos.netty.router;

import com.jkys.phobos.constant.ErrorEnum;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import com.jkys.phobos.remote.protocol.Response;
import com.jkys.phobos.server.PhobosContext;
import com.jkys.phobos.server.ServerInfo;
import com.jkys.phobos.util.SerializaionUtil;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
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
        String group = request.getRequest().getGroup();

        //client初次连接 返回server信息
        if(SERVER_INFO.equals(serviceName)&&SERVER_INFO.equals(methodName)&&SERVER_INFO_VER.equals(serviceVersion)){
            Map<String,Method> map = context.getMethodMap();
            ServerInfo serverInfo = new ServerInfo();
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()){
                serverInfo.getServiceList().add(context.getServerAppName() + "." + iterator.next());
            }
            phobosResponse.getResponse().setSuccess(true);
            phobosResponse.getResponse().setData(SerializaionUtil.objectToBytes(serverInfo,request.getHeader().getSerializationType()));
            return phobosResponse;
        }

        //调用client端请求的服务
        Method method = PhobosContext.getInstance().getMethod(serviceName,methodName,group,serviceVersion);
        if(method == null){
            phobosResponse.getResponse().setErrCode(ErrorEnum.UNKNOWN_METHOD.name());
            phobosResponse.getResponse().setSuccess(false);
            return phobosResponse;
        }

        List<byte[]> list = request.getRequest().getObject();
        int listSize = list == null ? 0 : list.size();
        Class<?>[] paramsType = method.getParameterTypes();
        if(paramsType.length!=listSize){
            phobosResponse.getResponse().setErrCode(ErrorEnum.INVALID_PARAMS.name());
            phobosResponse.getResponse().setSuccess(false);
            return phobosResponse;
        }
        Object[] params = new Object[paramsType.length];
        for(int i=0; i<paramsType.length; i++){
            try {
                params[i] = SerializaionUtil.bytesToObject(list.get(i), paramsType[i],request.getHeader().getSerializationType());
            }catch (Exception e){
                e.printStackTrace();
                phobosResponse.getResponse().setErrCode(ErrorEnum.INVALID_PARAMS.name());
                phobosResponse.getResponse().setSuccess(false);
                return phobosResponse;
            }
        }

        //Object service = method.getDeclaringClass().newInstance();
        Object service = PhobosContext.getInstance().getService(serviceName, group, serviceVersion).getService();

        Object value = null;
        try{
            value = method.invoke(service,params);
        }catch (Exception e){
            e.printStackTrace();
            phobosResponse.getResponse().setErrCode(ErrorEnum.SYSTEM_ERROR.name());
            phobosResponse.getResponse().setSuccess(false);
            return phobosResponse;
        }
        phobosResponse.getResponse().setData(SerializaionUtil.objectToBytes(value,request.getHeader().getSerializationType()));
        phobosResponse.getResponse().setSuccess(true);
        return phobosResponse;
    }
}
