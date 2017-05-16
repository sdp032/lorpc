package com.jkys.phobos.util;

/**
 * Created by lo on 5/16/17.
 */
public class Hex {
    private static final char[] HEX = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    public static String hexify(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            chars[i * 2] = HEX[b >> 4];
            chars[i * 2 + 1] = HEX[b & 0x0F];
        }
        return new String(chars);
    }
}
