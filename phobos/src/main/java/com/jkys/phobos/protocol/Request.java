package com.jkys.phobos.protocol;

import com.jkys.phobos.codec.SerializeHandle;
import com.jkys.phobos.codec.SerializeHandleFactory;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.util.CommonUtil;
import org.msgpack.annotation.Message;

import java.util.ArrayList;
import java.util.Arrays;
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

    static public Request toRequest(byte[] bytes) throws Exception{

        Request request = new Request();
        int index = 16;

        byte[] traceId = Arrays.copyOfRange(bytes,0, 16);
        request.setTraceId(traceId);

        byte[] serviceName = Arrays.copyOfRange(bytes, index + 1, bytes[index++] + index);
        index = index + serviceName.length;
        if(serviceName.length > 0){
            request.setServiceName(new String(serviceName, Request.CHARSET));
        }

        byte[] serviceVersion = Arrays.copyOfRange(bytes, index + 1 , bytes[index++] + index);
        index = index + serviceVersion.length;
        if(serviceVersion.length > 0){
            request.setServiceVersion(new String(serviceVersion, Request.CHARSET));
        }

        byte[] methodName = Arrays.copyOfRange(bytes, index + 1, bytes[index++] + index);
        index = index + methodName.length;
        if(methodName.length > 0){
            request.setMethodName(new String(methodName, Request.CHARSET));
        }

        byte[] clientAppName = Arrays.copyOfRange(bytes, index + 1, bytes[index++] + index);
        index = index + clientAppName.length;
        if(clientAppName.length > 0){
            request.setClientAppName(new String(clientAppName, Request.CHARSET));
        }

        byte[] signMethod = Arrays.copyOfRange(bytes, index + 1, bytes[index++] + index);
        index = index + signMethod.length;
        if(signMethod.length > 0){
            request.getSign().setMethod(new String(signMethod, Request.CHARSET));
        }

        byte[] signDigests = Arrays.copyOfRange(bytes, index + 1, bytes[index++] + index);
        index = index + signDigests.length;
        if(signDigests.length > 0){
            request.getSign().setDigests(new String(signDigests, Request.CHARSET));
        }

        byte[] params = Arrays.copyOfRange(bytes, index, bytes.length);

        List<Object> list = new ArrayList<>();
        list.add(params);
        request.setObject(list);
        return request;
    }
}
