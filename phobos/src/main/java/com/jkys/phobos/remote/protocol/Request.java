package com.jkys.phobos.remote.protocol;

import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.util.CommonUtil;
import com.jkys.phobos.util.SerializaionUtil;
import io.netty.buffer.ByteBuf;
import org.msgpack.annotation.Message;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.RawValue;
import org.msgpack.type.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by frio on 16/7/4.
 */
@Message
public class Request {

    private byte[] traceId;

    private String serviceName;

    private String serviceVersion;

    private String methodName;

    private String clientAppName;

    private Sign sign = new Sign();

    private String group;

    private List<byte[]> object; /*请求参数列表*/

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

    public List<byte[]> getObject() {
        return object;
    }

    public void setObject(List<byte[]> object) {
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

        byte[] serviceName = SerializaionUtil.objectToBytes(request.getServiceName(),type);
        byte[] serviceNameLen = new byte[]{(byte) (serviceName == null ? 0 : serviceName.length)};
        byte[] serviceVersion = SerializaionUtil.objectToBytes(request.getServiceVersion(),type);
        byte[] serviceVersionLen = new byte[]{(byte) (serviceVersion == null ? 0 : serviceVersion.length)};
        byte[] methodName = SerializaionUtil.objectToBytes(request.getMethodName(),type);
        byte[] methodNameLen = new byte[]{(byte) (methodName == null ? 0 : methodName.length)};
        byte[] clientAppName = SerializaionUtil.objectToBytes(request.getClientAppName(),type);
        byte[] clientAppNameLen = new byte[]{(byte) (clientAppName == null ? 0 : clientAppName.length)};
        byte[] signMethod = SerializaionUtil.objectToBytes(request.getSign().getMethod(),type);
        byte[] signMethodLen = new byte[]{(byte)(signMethod == null ? 0 : signMethod.length)};
        byte[] signDigests = SerializaionUtil.objectToBytes(request.getSign().getDigests(),type);
        byte[] signDigestsLen = new byte[]{(byte)(signDigests == null ? 0 : signDigests.length)};
        byte[] params = MsgpackUtil.MESSAGE_PACK.write(request.getObject());

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
        in.readBytes(serviceName);
        request.setServiceName(SerializaionUtil.bytesToObject(serviceName, String.class,type));

        byte[] serviceVersion = new byte[in.readByte()];
        in.readBytes(serviceVersion);
        request.setServiceVersion(SerializaionUtil.bytesToObject(serviceVersion, String.class, type));

        byte[] methodName = new byte[in.readByte()];
        in.readBytes(methodName);
        request.setMethodName(SerializaionUtil.bytesToObject(methodName, String.class, type));

        byte[] clientAppName = new byte[in.readByte()];
        in.readBytes(clientAppName);
        request.setClientAppName(SerializaionUtil.bytesToObject(clientAppName, String.class, type));

        byte[] signMethod = new byte[in.readByte()];
        if(signMethod.length > 0){
            in.readBytes(signMethod);
            request.getSign().setMethod(SerializaionUtil.bytesToObject(signMethod, String.class, type));
        }

        byte[] signDigests = new byte[in.readByte()];
        if(signDigests.length > 0){
            in.readBytes(signDigests);
            request.getSign().setDigests(SerializaionUtil.bytesToObject(signDigests, String.class, type));
        }

        byte[] params = new byte[
                size - 16 - 6 - serviceName.length - serviceVersion.length - methodName.length - clientAppName.length - signMethod.length - signDigests.length];

        if(params.length > 0){
            in.readBytes(params);
            Value value = MsgpackUtil.MESSAGE_PACK.read(params);
            if(value.isNilValue()){
                request.setObject(null);
            }else {
                ArrayList<byte[]> list = new ArrayList<>();
                Value[] vs = value.asArrayValue().getElementArray();
                for(Value v : vs){
                    list.add(v.asRawValue().getByteArray());
                    request.setObject(list);
                }
            }
        }

        return request;
    }
}
