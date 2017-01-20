package com.jkys.phobos.annotation;

/**
 * Created by lo on 1/9/17.
 */
public class ServiceUtil {
    public static String[] splitServiceKey(String value) {
        String[] parts = value.split(":");
        if (parts.length != 2) {
            // FIXME
            throw new RuntimeException("invalid service: " + value);
        }
        return parts;
    }

    public static String[] splitServiceKey(Service service) {
        return splitServiceKey(service.value());
    }

    public static String getName(Service service) {
        return splitServiceKey(service.value())[0];
    }

    public static String getVersion(Service service) {
        return splitServiceKey(service.value())[1];
    }

    public static String serviceKey(String name, String version) {
        return name + ":" + version;
    }
}
