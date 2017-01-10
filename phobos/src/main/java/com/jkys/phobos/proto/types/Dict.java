package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;
import com.jkys.phobos.proto.ValueNotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Created by lo on 1/10/17.
 */
public class Dict extends ProtoType {
    private ProtoType keyType;
    private ProtoType valueType;

    Dict(ProtoContext ctx, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
        if (!(type instanceof ParameterizedType)) {
            // FIXME
            throw new RuntimeException("unknown map types");
        }
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        keyType = TypeResolver.resolve(ctx, types[0], null);
        // TODO key type nullable
        keyType.setNullable(false);
        valueType = TypeResolver.resolve(ctx, types[2], null);
        valueType.setNullable(ele.getAnnotation(ValueNotNull.class) == null);
    }

    @Override
    public String name() {
        return "map<" + keyType.name() + ", " + valueType.name() + ">";
    }
}
