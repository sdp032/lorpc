package com.jkys.phobos.util;

import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.remote.protocol.Header;

/**
 * Created by zdj on 2016/7/18.
 */
public class SerializaionUtil {

    public static byte[] objectToBytes(Object o,char serializationType) throws Exception{
        byte[] bytes = null;
        if(serializationType == Header.SerializationType.MAGPACK.serializationType){
            bytes = MsgpackUtil.MESSAGE_PACK.write(o);
        }
        //TODO 其他序列化方式
        return  bytes;
    }

    public static <T> T bytesToObject(byte[] bytes,Class<T> clazz,char serializationType) throws Exception{
        T t = null;
        if(serializationType == Header.SerializationType.MAGPACK.serializationType){
            t = MsgpackUtil.MESSAGE_PACK.read(bytes,clazz);
        }
        //TODO 其他反序列化方式
        return t;
    }
}
