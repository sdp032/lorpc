package com.jkys.phobos.proto.types;

import com.jkys.phobos.exception.TypeUnsupported;
import com.jkys.phobos.proto.EleNotNull;
import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by lo on 1/13/17.
 */
public class Set extends ProtoType {
    private ProtoType elementType;

    Set(ProtoContext ctx, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
        if (type instanceof ParameterizedType) {
            Type eleType = ((ParameterizedType) type).getActualTypeArguments()[0];
            ctx.pushElement("<item>");
            elementType = TypeResolver.resolve(ctx, eleType, null);
            ctx.popElement();
        } else {
            throw new TypeUnsupported(type, "unknown set: " + type.getTypeName());
        }
        if (ele != null) {
            elementType.setNullable(ele.getAnnotation(EleNotNull.class) == null);
        } else {
            elementType.setNullable(true);
        }
    }

    @Override
    public String name() {
        return "set";
    }

    @Override
    public Map<String, Object> dump() {
        Map<String, Object> result = super.dump();
        result.put("item", elementType.dump());
        return result;
    }
}
