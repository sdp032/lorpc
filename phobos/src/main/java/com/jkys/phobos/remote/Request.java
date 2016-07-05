package com.jkys.phobos.remote;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by frio on 16/7/4.
 */
public class Request {

    private String version; /** service version **/

    private long sequenceId; /** request sequence id **/

    private long timestamp;

    private byte serializationType;

    private InvokeType invokeType; /** one way,two way and so on **/

    private byte requestType;

    private Object data;

    private static final AtomicLong INVOKE_ID = new AtomicLong(0);

    public Request(){
        this.sequenceId = INVOKE_ID.getAndIncrement();
    }

    public enum InvokeType {
        ONEWAY(1), TWOWAY(2);
        private int type;

        InvokeType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static InvokeType getByType(int type) {
            InvokeType[] values = InvokeType.values();
            for (InvokeType v : values) {
                if (v.type == type) {
                    return v;
                }
            }
            return ONEWAY;
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(byte serializationType) {
        this.serializationType = serializationType;
    }

    public InvokeType getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(InvokeType invokeType) {
        this.invokeType = invokeType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public byte getRequestType() {
        return requestType;
    }

    public void setRequestType(byte requestType) {
        this.requestType = requestType;
    }
}
