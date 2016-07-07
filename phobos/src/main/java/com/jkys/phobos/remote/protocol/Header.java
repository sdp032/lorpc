package com.jkys.phobos.remote.protocol;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by frio on 16/7/4.
 */
public class Header {

    private final short protocolVersion = 1;

    private final long sequenceId;

    private char serializationType;

    private char type;

    private int size;

    private long timestamp;

    private static final AtomicLong INVOKE_ID = new AtomicLong(0);

    public Header(){
        sequenceId = INVOKE_ID.getAndIncrement();
    }

    public enum SerializationType{

        JSON('1'),MAGPACK('2'),PROTOBUFF('3');

        public final char serializationType;

        private SerializationType(char serializationType){
            this.serializationType = serializationType;
        }

        public char getSerializationType() {
            return serializationType;
        }
    }

    public enum Type{
        DEFAULT('0'),WITHSTREAM('1'),STREAM('2'),CLOSESTREAM('3');

        public final char type;

        private Type(char type){
            this.type = type;
        }

        public char getType(){
            return type;
        }
    }

    public short getProtocolVersion() {
        return protocolVersion;
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public char getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(char serializationType) {
        this.serializationType = serializationType;
    }
}
