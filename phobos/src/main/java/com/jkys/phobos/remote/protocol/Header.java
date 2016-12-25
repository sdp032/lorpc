package com.jkys.phobos.remote.protocol;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by frio on 16/7/4.
 */
public class Header {

    private static final AtomicLong INVOKE_ID = new AtomicLong(1);
    private short protocolVersion = 1;
    private byte serializationType;
    private byte type;
    private int size;
    private long sequenceId;
    private long timestamp;

    public Header() {
        sequenceId = INVOKE_ID.getAndIncrement();
    }

    public short getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(short protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(byte serializationType) {
        this.serializationType = serializationType;
    }

    public enum SerializationType {

        JSON((byte) 1), MAGPACK((byte) 2), PROTOBUFF((byte) 3);

        public final byte serializationType;

        private SerializationType(byte serializationType) {
            this.serializationType = serializationType;
        }

        public byte getSerializationType() {
            return serializationType;
        }
    }

    public enum Type {
        DEFAULT((byte) 0), WITHSTREAM((byte) 1), STREAM((byte) 2), CLOSESTREAM((byte) 3);

        public final byte type;

        private Type(byte type) {
            this.type = type;
        }

        public byte getType() {
            return type;
        }
    }
}
