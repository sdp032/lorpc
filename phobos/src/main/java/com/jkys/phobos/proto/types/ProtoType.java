package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.NotNull;
import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.util.Set;

/**
 * Created by lo on 1/10/17.
 */
public abstract class ProtoType {
    private boolean nullable = false;

    ProtoType(ProtoContext ctx, Class<?> cls, AnnotatedElement ele) {
        this.nullable = isNullable(cls, ele);
    }

    private static boolean isNullable(Class<?> cls, AnnotatedElement ele) {
        if (cls.isPrimitive()) {
            return false;
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
