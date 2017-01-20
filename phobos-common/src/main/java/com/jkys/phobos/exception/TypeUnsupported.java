package com.jkys.phobos.exception;

import java.lang.reflect.Type;

/**
 * Created by lo on 1/17/17.
 */
public class TypeUnsupported extends RuntimeException {
    private Type type;

    public TypeUnsupported(Type type, String message) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
