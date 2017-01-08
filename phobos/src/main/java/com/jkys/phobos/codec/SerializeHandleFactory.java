package com.jkys.phobos.codec;

/**
 * Created by zdj on 16-12-26.
 */
public class SerializeHandleFactory {

    public static SerializeHandle create(byte type){
        if (type == SerializationType.Msgpack.getType()) {
            return MsgpackSerializeHandle.getInstance();
        }

        return null;
    }
}
