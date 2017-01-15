package com.jkys.phobos.serialization;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lo on 1/15/17.
 */
public class SerializerFactory {
    private ConcurrentHashMap<Type, Serializer> jsonSerializers = new ConcurrentHashMap<>();

    public Serializer get(SerializationType serializationType, Type targetType) {
        switch (serializationType) {
            case Json:
                Serializer serializer = jsonSerializers.get(targetType);
                if (serializer == null) {
                    serializer = new JsonSerializer(targetType);
                    jsonSerializers.putIfAbsent(targetType, serializer);
                }
                return serializer;
            default:
                throw new RuntimeException("unsupported serialization type: " + targetType);
        }
    }
}
