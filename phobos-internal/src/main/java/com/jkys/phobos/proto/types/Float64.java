package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Created by lo on 1/10/17.
 */
public class Float64 extends ProtoType {
    Float64(ProtoContext ctx, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
    }

    @Override
    public String name() {
        return "float64";
    }
}
