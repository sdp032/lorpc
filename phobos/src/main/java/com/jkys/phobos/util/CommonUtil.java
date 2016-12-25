package com.jkys.phobos.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Created by zdj on 16-12-8.
 */
public class CommonUtil {

    public static String getIpAddresses() throws Exception{
        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        while (allNetInterfaces.hasMoreElements()){
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()){
                ip = (InetAddress) addresses.nextElement();
                if (ip != null && ip instanceof Inet4Address){
                    return ip.getHostAddress();
                }
            }
        }

        throw new RuntimeException("Not found qualified IP");
    }

    public static <T> T[] concatAll(T[] first, T[]... rest) {
        int totalLength = first.length;
        for (T[] array : rest) {
            totalLength += array.length;
        }
        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (T[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    public static byte[] concatBytes(byte[] first, byte[]... rest){

        int totalLength = first.length;
        for (byte[] array : rest) {
            if(array != null)
                totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            if(array != null ){
                System.arraycopy(array, 0, result, offset, array.length);
                offset += array.length;
            }
        }
        return result;
    }
}
