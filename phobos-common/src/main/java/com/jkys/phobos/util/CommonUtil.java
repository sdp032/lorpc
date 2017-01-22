package com.jkys.phobos.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * Created by zdj on 16-12-8.
 */
public class CommonUtil {

    public static String getIpAddresses() throws Exception{
        List<InetAddress> ips = new ArrayList<>();
        Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        while (allNetInterfaces.hasMoreElements()){
            NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
            Enumeration addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()){
                InetAddress ip = (InetAddress) addresses.nextElement();
                if (ip != null && !ip.isLinkLocalAddress()){
                    ips.add(ip);
                }
            }
        }
        if (ips.size() == 0) {
            throw new RuntimeException("Not found qualified IP");
        }

        ips.sort((InetAddress a, InetAddress b) -> {
            if (a.isLoopbackAddress()) {
                return 1;
            }
            if (a.getHostAddress().startsWith("172")) {
                return 1;
            }
            if (b.getHostAddress().startsWith("172")) {
                return -1;
            }
            return a.getHostAddress().compareTo(b.getHostAddress());
        });
        return ips.get(0).getHostAddress();
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
