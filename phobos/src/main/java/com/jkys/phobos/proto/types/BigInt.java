package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Created by lo on 1/10/17.
 */
public class BigInt extends ProtoType {
    BigInt(ProtoContext ctx, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
    }

    @Override
    public String name() {
        return "bigint";
    }
}
