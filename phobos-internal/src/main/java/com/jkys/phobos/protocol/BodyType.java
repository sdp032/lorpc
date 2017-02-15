package com.jkys.phobos.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lo on 2/13/17.
 */
public enum BodyType {
    Default((byte) 0),
    WithStream((byte) 1),
    Stream((byte) 2),
    CloseStream((byte) 3),
    Shutdown((byte) 4);

    private final byte type;
    private static Map<Byte, BodyType> typeMap = new HashMap<>();

    BodyType(byte type) {
        this.type = type;
    }

    public static BodyType getBodyType(byte t) {
        BodyType result = typeMap.get(t);
        if (result == null) {
            if (typeMap.size() == 0) {
                synchronized (BodyType.class) {
                    if (typeMap.size() == 0) {
                        for (BodyType bodyType : BodyType.values()) {
                            typeMap.put(bodyType.type, bodyType);
                        }
                    }
                }
                result = typeMap.get(t);
            }
        }
        return result;
    }

    public byte getType() {
        return this.type;
    }
}
