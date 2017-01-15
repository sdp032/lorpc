package com.jkys.phobos.serialization;

/**
 * Created by lo on 1/15/17.
 */
public interface Serializer {
    byte[] encode(Object object);
    Object decode(byte[] data);
    Object[] decodeArray(byte[] data);
}
