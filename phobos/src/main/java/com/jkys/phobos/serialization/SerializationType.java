package com.jkys.phobos.serialization;

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

    public static SerializationType get(byte type) {
        switch (type) {
            case 1:
                return Json;
            case 2:
                return Msgpack;
            case 3:
                return Protobuf;
        }
        return null;
    }
}
