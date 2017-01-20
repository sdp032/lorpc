package com.jkys.phobos.proto;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lo on 1/15/17.
 */
public class ParamsType implements ParameterizedType {
    private static Map<Class, Class> primitives = new HashMap<>();
    static {
        primitives.put(boolean.class, Boolean.class);
        primitives.put(byte.class, Byte.class);
        primitives.put(char.class, Character.class);
        primitives.put(short.class, Short.class);
        primitives.put(int.class, Integer.class);
        primitives.put(long.class, Long.class);
        primitives.put(float.class, Float.class);
        primitives.put(double.class, Double.class);
    }

    public interface Params {}

    private Type[] types;

    public ParamsType(Type[] types) {
        this.types = new Type[types.length];
        for (int i = 0; i < types.length; i++) {
            this.types[i] = types[i];
            if (types[i] instanceof Class) {
                Class cls = (Class) types[i];
                if (cls.isPrimitive()) {
                    this.types[i] = primitives.get(cls);
                }
            }
        }
    }

    public Type[] getTypes() {
        return types;
    }

    @Override
    public String getTypeName() {
        List<String> names = new ArrayList<>();
        for (Type type : types) {
            names.add(type.getTypeName());
        }
        return "Params<" + String.join(", ", names) + ">";
    }

    @Override
    public Type[] getActualTypeArguments() {
        return types;
    }

    @Override
    public Type getRawType() {
        return Params.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
