package com.jkys.phobos.spring.client.Handler.impl;

import com.jkys.phobos.annotation.PhobosGroup;
import com.jkys.phobos.annotation.PhobosVersion;
import com.jkys.phobos.client.InvokeInfo;
import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.codec.SerializeHandle;
import com.jkys.phobos.codec.SerializeHandleFactory;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.netty.NettyClient;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.Request;
import com.jkys.phobos.server.PhobosContext;
import com.jkys.phobos.spring.client.Handler.PhobosHandler;
import com.jkys.phobos.util.SerializaionUtil;
import org.msgpack.type.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by zdj on 2016/7/1.
 */
public class DefaultPhobosHandler implements PhobosHandler {

    private static Logger logger = LoggerFactory.getLogger(DefaultPhobosHandler.class);

    private String serviceAppName;

    public DefaultPhobosHandler() {
    }

    public DefaultPhobosHandler(String serviceAppName) {
        this.serviceAppName = serviceAppName;
    }

    public Object execution(Method method, Object[] args) throws Exception {

        logger.info("executing ...");

        PhobosGroup phobosGroupAnnotation = method.getAnnotation(PhobosGroup.class) == null ?
                method.getDeclaringClass().getAnnotation(PhobosGroup.class) :
                method.getAnnotation(PhobosGroup.class);
        if (phobosGroupAnnotation == null)
            throw new NullPointerException("PhobosGroup is null for " + method.getDeclaringClass().getName());

        PhobosVersion phobosVersionAnnotation = method.getAnnotation(PhobosVersion.class) == null ?
                method.getDeclaringClass().getAnnotation(PhobosVersion.class) :
                method.getAnnotation(PhobosVersion.class);
        if (phobosVersionAnnotation == null)
            throw new NullPointerException("PhobosVersion is null for " + method.getDeclaringClass().getName());

        String version = phobosVersionAnnotation.version();
        String group = phobosGroupAnnotation.value();

        String serveiceKey = PhobosContext.generateMethodKey(
                serviceAppName + "." + method.getDeclaringClass().getName(),
                method.getName(),
                group,
                version,
                method.getParameterTypes()
        );

        logger.info("serveiceKey is {}", serveiceKey);

        List<NettyClient> clientList = PhobosClientContext.getInstance().getConnectInfo().get(serveiceKey);
        if (null == clientList || clientList.size() == 0) {
            throw new NullPointerException("client list is null for " + serveiceKey);
        }
        NettyClient client = clientList.get(new Random().nextInt(clientList.size()));

        //参数转化成字节流
        /*List<byte[]> params = null;
        if (args != null && args.length > 0) {
            params = new ArrayList();
            for (Object o : args) {
                params.add(SerializaionUtil.objectToBytes(o, PhobosClientContext.getInstance().getSerializationType()));
            }
        }*/
        List<Object> params = new ArrayList<>();
        if(args != null && args.length > 0){
            Collections.addAll(params, args);
        }

        StringBuffer methodName = new StringBuffer();
        methodName.append(method.getName());
        /*methodName.append("(");
        for (int i=0; i<method.getParameterTypes().length; i++){
            methodName.append(method.getParameterTypes()[i].getName());
            if(i < method.getParameterTypes().length - 1){
                methodName.append(",");
            }
        }
        methodName.append(")");*/

        logger.info("methodName is {}", methodName.toString());
        logger.info("params is {}", args);

        PhobosRequest request = new PhobosRequest(new Header(), new Request());
        request.getHeader().setSerializationType(PhobosClientContext.getInstance().getSerializationType());
        request.getRequest().setClientAppName(PhobosClientContext.getInstance().getClientAppName());
        request.getRequest().setServiceName(method.getDeclaringClass().getName());
        request.getRequest().setMethodName(methodName.toString());
        request.getRequest().setGroup(group);
        request.getRequest().setServiceVersion(version);
        request.getRequest().setTraceId(new byte[16]); //TODO 规则待定
        request.getRequest().setObject(params);

        InvokeInfo invokeInfo = client.send(request);

        //服务端异常
        if(!invokeInfo.getResponse().getResponse().isSuccess()){

            logger.error("{} : {}" , serveiceKey, methodName);
            logger.error("err code: {}, err message: {}", invokeInfo.getResponse().getResponse().getErrCode(), invokeInfo.getResponse().getResponse().getErrMessage());
            throw new PhobosException(invokeInfo.getResponse().getResponse().getErrCode(), invokeInfo.getResponse().getResponse().getErrMessage());
        }

        if (method.getReturnType() == Void.TYPE) {
            return null;
        }

        SerializeHandle handle = SerializeHandleFactory.create(PhobosClientContext.getInstance().getSerializationType());

        return handle.bytesToReturnVal(invokeInfo.getResponse().getResponse().getData(), method.getReturnType(), method.getGenericReturnType());
    }

    public String getServiceAppName() {
        return serviceAppName;
    }

    public void setServiceAppName(String serviceAppName) {
        this.serviceAppName = serviceAppName;
    }
}
