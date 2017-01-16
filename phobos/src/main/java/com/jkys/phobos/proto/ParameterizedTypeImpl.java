package com.jkys.phobos.proto;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by lo on 1/11/17.
 */
public class ParameterizedTypeImpl implements ParameterizedType {
    private Type rawType;
    private Type typeArguments[];

    public ParameterizedTypeImpl(Type rawType, Type[] typeArguments) {
        this.rawType = rawType;
        this.typeArguments = typeArguments;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        throw new TypeNotPresentException(getTypeName(), null);
    }

    @Override
    public String getTypeName() {
        String names[] = new String[typeArguments.length];
        for (int i = 0; i < typeArguments.length; i++) {
            names[i] = typeArguments[i].getTypeName();
        }
        return rawType.getTypeName() + "<" + String.join(", ", names) + ">";
    }
}
