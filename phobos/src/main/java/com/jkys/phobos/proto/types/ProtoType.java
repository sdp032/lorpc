package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.NotNull;
import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Created by lo on 1/10/17.
 */
public abstract class ProtoType {
    private boolean nullable = false;

    ProtoType(ProtoContext ctx, Type type, AnnotatedElement ele) {
        this.nullable = isNullable(type, ele);
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

    public abstract String name();

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}
