package com.jkys.phobos.codec;

import com.jkys.phobos.remote.protocol.Header;

/**
 * Created by zdj on 16-12-26.
 */
public class SerializeHandleFactory {

    public static SerializeHandle create(byte type){
        if(type == Header.SerializationType.MAGPACK.serializationType){
            return MsgpackSerializeHandle.getInstance();
        }

        return null;
    }
}
