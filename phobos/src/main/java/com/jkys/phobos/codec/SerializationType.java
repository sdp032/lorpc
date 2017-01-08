package com.jkys.phobos.codec;

/**
 * Created by lo on 1/5/17.
 */
public enum SerializationType {
    Json(1),
    Msgpack(2),
    Protobuf(3);

    private byte type;

    SerializationType(int type) {
        this.type = (byte) type;
    }

    public byte getType() {
        return type;
    }

    public SerializationType get(String name) {
        if ("JSON".equals(name)) {
            return Json;
        }
        if ("MSGPACK".equals(name)) {
            return Msgpack;
        }
        if ("PROTOBUF".equals(name)) {
            return Protobuf;
        }
        return null;
    }
}
