package com.jkys.phobos.remote.protocol;

import org.msgpack.annotation.Message;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by frio on 16/7/4.
 */
@Message
public class Request {

    private long traceId;

    private String serviceName;

    private String serviceVersion;

    private String methodName;

    private String clientAppName;

    private List<byte[]> object; /*请求参数列表*/

    public long getTraceId() {
        return traceId;
    }

    public void setTraceId(long traceId) {
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

    public List<byte[]> getObject() {
        return object;
    }

    public void setObject(List<byte[]> object) {
        this.object = object;
    }
}
