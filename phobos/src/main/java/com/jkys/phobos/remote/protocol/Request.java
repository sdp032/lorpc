package com.jkys.phobos.remote.protocol;

import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.codec.SerializeHandle;
import com.jkys.phobos.codec.SerializeHandleFactory;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.server.PhobosContext;
import com.jkys.phobos.util.CommonUtil;
import com.jkys.phobos.util.SerializaionUtil;
import io.netty.buffer.ByteBuf;
import org.msgpack.annotation.Message;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.RawValue;
import org.msgpack.type.Value;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by frio on 16/7/4.
 */
@Message
public class Request {

    private static final String CHARSET = "UTF-8";

    private byte[] traceId;

    private String serviceName;

    private String serviceVersion;

    private String methodName;

    private String clientAppName;

    private Sign sign = new Sign();

    private String group;

    //private List<byte[]> object; /*请求参数列表*/
    private List<Object> object; /*请求参数列表*/

    public byte[] getTraceId() {
        return traceId;
    }

    public void setTraceId(byte[] traceId) {
        this.traceId = traceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClientAppName() {
        return clientAppName;
    }

    public void setClientAppName(String clientAppName) {
        this.clientAppName = clientAppName;
    }

    public Sign getSign() {
        return sign;
    }

    public void setSign(Sign sign) {
        this.sign = sign;
    }

   /* public List<byte[]> getObject() {
        return object;
    }

    public void setObject(List<byte[]> object) {
        this.object = object;
    }*/

    public List<Object> getObject() {
        return object;
    }

    public void setObject(List<Object> object) {
        this.object = object;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Message
    static public class Sign{

        private String method;
        private String digests;

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getDigests() {
            return digests;
        }

        public void setDigests(String digests) {
            this.digests = digests;
        }
    }

    static public byte[] toBytes(Request request, byte type) throws Exception{

        byte[] traceId = request.traceId;

        if(request.getTraceId() == null || request.getTraceId().length != 16){
            throw new PhobosException(null, "traceId wrongful");
        }

        SerializeHandle handle = SerializeHandleFactory.create(type);

        byte[] serviceName = request.getServiceName() == null ? new byte[0] : request.getServiceName().getBytes(Request.CHARSET);
        byte[] serviceNameLen = new byte[]{(byte)serviceName.length};
        byte[] serviceVersion = request.getServiceVersion() == null ? new byte[0] : request.getServiceVersion().getBytes(Request.CHARSET);
        byte[] serviceVersionLen = new byte[]{(byte) serviceVersion.length};
        byte[] methodName = request.getMethodName() == null ? new byte[0] : request.getMethodName().getBytes(Request.CHARSET) ;
        byte[] methodNameLen = new byte[]{(byte)methodName.length};
        byte[] clientAppName = request.getClientAppName() == null ? new byte[0] : request.getClientAppName().getBytes(Request.CHARSET);
        byte[] clientAppNameLen = new byte[]{(byte)clientAppName.length};
        byte[] signMethod = request.getSign().getMethod() == null ? new byte[0] : request.getSign().getMethod().getBytes(Request.CHARSET);
        byte[] signMethodLen = new byte[]{(byte)signMethod.length};
        byte[] signDigests = request.getSign().getDigests() == null ? new byte[0] : request.getSign().getDigests().getBytes(Request.CHARSET);
        byte[] signDigestsLen = new byte[]{(byte)signDigests.length};
        /*byte[] params = MsgpackUtil.MESSAGE_PACK.write(request.getObject());*/
        byte[] params = handle.objectToBytes(request.getObject());

        byte[] body = CommonUtil.concatBytes(traceId, serviceNameLen, serviceName, serviceVersionLen, serviceVersion,
                methodNameLen, methodName, clientAppNameLen, clientAppName, signMethodLen, signMethod, signDigestsLen, signDigests, params);

        return body;
    }

    static public Request toRequest(ByteBuf in, byte type, int size) throws Exception{

        Request request = new Request();

        byte[] traceId = new byte[16];
        in.readBytes(traceId);
        request.setTraceId(traceId);

        byte[] serviceName = new byte[in.readByte()];
        if(serviceName.length > 0){
            in.readBytes(serviceName);
            request.setServiceName(new String(serviceName, Request.CHARSET));
        }

        byte[] serviceVersion = new byte[in.readByte()];
        if(serviceVersion.length > 0){
            in.readBytes(serviceVersion);
            request.setServiceVersion(new String(serviceVersion, Request.CHARSET));
        }

        byte[] methodName = new byte[in.readByte()];
        if(methodName.length > 0){
            in.readBytes(methodName);
            request.setMethodName(new String(methodName, Request.CHARSET));
        }

        byte[] clientAppName = new byte[in.readByte()];
        if(clientAppName.length > 0){
            in.readBytes(clientAppName);
            request.setClientAppName(new String(clientAppName, Request.CHARSET));
        }

        byte[] signMethod = new byte[in.readByte()];
        if(signMethod.length > 0){
            in.readBytes(signMethod);
            request.getSign().setMethod(new String(signMethod, Request.CHARSET));
        }

        byte[] signDigests = new byte[in.readByte()];
        if(signDigests.length > 0){
            in.readBytes(signDigests);
            request.getSign().setDigests(new String(signDigests, Request.CHARSET));
        }

        byte[] params = new byte[
                size - 16 - 6 - serviceName.length - serviceVersion.length - methodName.length - clientAppName.length - signMethod.length - signDigests.length];

        if(params.length > 0){
            in.readBytes(params);
        }

        List<Object> list = new ArrayList<>();
        list.add(params);
        request.setObject(list);
        return request;
    }
}
