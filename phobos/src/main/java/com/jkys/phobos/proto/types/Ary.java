package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.EleNotNull;
import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;

/**
 * Created by lo on 1/10/17.
 */
public class Ary extends ProtoType {
    private ProtoType elementType;

    Ary(ProtoContext ctx, Class<?> cls , AnnotatedElement ele) {
        super(ctx, cls, ele);
        elementType = TypeResolver.resolve(ctx, cls.getComponentType(), null);
        elementType.setNullable(ele.getAnnotation(EleNotNull.class) == null);
    }

    @Override
    public String name() {
        return "[" + elementType.name() + "]";
    }
}
