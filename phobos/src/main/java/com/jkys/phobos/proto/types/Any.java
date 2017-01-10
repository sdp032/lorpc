package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Created by lo on 1/10/17.
 */
public class Any extends ProtoType {
    private static final String[] DEFAULT_TYPES = new String[]{
            "i32", "i64", "bool", "string", "float32", "float64", "bigint", "bytes"};
    private String[] validTypes;

    Any(ProtoContext ctx, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
        validTypes = DEFAULT_TYPES;
    }

    @Override
    public String name() {
        return "any<" + String.join(", ", validTypes) + ">";
    }
}
