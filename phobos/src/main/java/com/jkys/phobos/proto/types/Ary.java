package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.EleNotNull;
import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by lo on 1/10/17.
 */
public class Ary extends ProtoType {
    private ProtoType elementType;

    Ary(ProtoContext ctx, Type type , AnnotatedElement ele) {
        super(ctx, type, ele);
        if (type.getTypeName().startsWith("[") && type instanceof Class) {
            elementType = TypeResolver.resolve(ctx, ((Class) type).getComponentType(), null);
        } else if (type instanceof ParameterizedType) {
            Type eleType = ((ParameterizedType) type).getActualTypeArguments()[0];
            elementType = TypeResolver.resolve(ctx, eleType, null);
        }
        elementType.setNullable(ele.getAnnotation(EleNotNull.class) == null);
    }

    @Override
    public String name() {
        return "[" + elementType.name() + "]";
    }
}
