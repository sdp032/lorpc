package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;
import com.jkys.phobos.proto.ValueNotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.TypeVariable;

/**
 * Created by lo on 1/10/17.
 */
public class Dict extends ProtoType {
    private ProtoType keyType;
    private ProtoType valueType;

    Dict(ProtoContext ctx, Class<?> cls, AnnotatedElement ele) {
        super(ctx, cls, ele);
        TypeVariable<? extends Class<?>>[] vars = cls.getTypeParameters();
        keyType = TypeResolver.resolve(ctx, vars[0].getClass(), null);
        valueType = TypeResolver.resolve(ctx, vars[1].getClass(), null);
        valueType.setNullable(ele.getAnnotation(ValueNotNull.class) == null);
    }

    @Override
    public String name() {
        return "map<" + keyType.name() + ", " + valueType.name() + ">";
    }
}
