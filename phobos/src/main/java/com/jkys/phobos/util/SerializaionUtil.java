package com.jkys.phobos.util;

/**
 * Created by zdj on 2016/7/18.
 */
public class SerializaionUtil {

    /*public static byte[] objectToBytes(Object o, byte serializationType) throws Exception {
        byte[] bytes = null;
        //if (o == null) return bytes;
        if (serializationType == Header.SerializationType.MAGPACK.serializationType) {
            if(o instanceof HashMap){
                o = new HashMapMsgpackTemplate((HashMap) o);
            }else if(o instanceof ArrayList){
                o = new ArrayListMsgpackTemplate((ArrayList) o);
            }
            bytes = MsgpackUtil.MESSAGE_PACK.write(o);
        }
        //TODO 其他序列化方式
        return bytes;
    }

    public static <T> T bytesToObject(byte[] bytes, Class<T> clazz, byte serializationType) throws Exception {
        T t = null;
        if(bytes == null ) return  t;
        if (serializationType == Header.SerializationType.MAGPACK.serializationType) {
            if(clazz == HashMap.class || clazz == Map.class){
                t = (T)MsgpackUtil.MESSAGE_PACK.read(bytes, HashMapMsgpackTemplate.class).toHashMap();
            }else if(clazz == ArrayList.class || clazz == List.class){
                t = (T)MsgpackUtil.MESSAGE_PACK.read(bytes, ArrayListMsgpackTemplate.class).toArrayList();
            }else {
                t = MsgpackUtil.MESSAGE_PACK.read(bytes, clazz);
            }

        }
        //TODO 其他反序列化方式
        return t;
    }*/

}
