package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;

/**
 * Created by lo on 1/10/17.
 */
public class BigInt extends ProtoType {
    BigInt(ProtoContext ctx, Class<?> cls, AnnotatedElement ele) {
        super(ctx, cls, ele);
    }

    @Override
    public String name() {
        return "bigint";
    }
}
