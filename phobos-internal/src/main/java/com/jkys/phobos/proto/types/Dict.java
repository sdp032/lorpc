package com.jkys.phobos.proto.types;

import com.jkys.phobos.exception.TypeUnsupported;
import com.jkys.phobos.proto.ProtoContext;
import com.jkys.phobos.proto.ValueNotNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by lo on 1/10/17.
 */
public class Dict extends ProtoType {
    private ProtoType keyType;
    private ProtoType valueType;

    Dict(ProtoContext ctx, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
        if (!(type instanceof ParameterizedType)) {
            throw new TypeUnsupported(type, "unknown map type: " + ctx.elements());
        }
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        ctx.pushElement("<key>");
        keyType = TypeResolver.resolve(ctx, types[0], null);
        ctx.popElement();
        // TODO key type nullable
        keyType.setNullable(false);
        ctx.pushElement("<value>");
        valueType = TypeResolver.resolve(ctx, types[1], null);
        ctx.popElement();
        if (ele != null) {
            valueType.setNullable(ele.getAnnotation(ValueNotNull.class) == null);
        } else {
            valueType.setNullable(true);
        }
    }

    @Override
    public String name() {
        return "map";
    }

    @Override
    public Map<String, Object> dump() {
        Map<String, Object> result = super.dump();
        result.put("key", keyType.dump());
        result.put("value", valueType.dump());
        return result;
    }
}
