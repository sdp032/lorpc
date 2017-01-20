package com.jkys.phobos.proto.types;

import com.jkys.phobos.annotation.NotNull;
import com.jkys.phobos.proto.ProtoContext;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lo on 1/10/17.
 */
public abstract class ProtoType {
    private boolean nullable = false;
    private Type rawType;

    ProtoType(ProtoContext ctx, Type type, AnnotatedElement ele) {
        this.nullable = isNullable(type, ele);
        this.rawType = type;
    }

    private static boolean isNullable(Type type, AnnotatedElement ele) {
        if (type instanceof Class) {
            if (((Class<?>) type).isPrimitive()) {
                return false;
            }
        }
        if (ele == null) {
            return true;
        }
        return ele.getAnnotation(NotNull.class) == null;
    }

    public String toString() {
        return name();
    }

    public abstract String name();

    public Map<String, Object> dump() {
        Map<String, Object> result = new HashMap<>();
        result.put("type", name());
        if (nullable) {
            result.put("nullable", true);
        }
        return result;
    }

    public Map<String, Object> dumpObject() {
        throw new NotImplementedException();
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Type getRawType() {
        return rawType;
    }
}
