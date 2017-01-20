package com.jkys.phobos.util;

/**
 * Created by zdj on 16-12-26.
 */
public class ByteUtil {
    public static byte[] shortToBytes(short data){
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }

    public static short bytesToShort(byte[] bytes){
        if(bytes == null || bytes.length != 2){
            throw new RuntimeException("数组长度必须为2");
        }
        return  (short) ((bytes[0] & 0xff) | (bytes[1] & 0xff) << 8);
    }
}
