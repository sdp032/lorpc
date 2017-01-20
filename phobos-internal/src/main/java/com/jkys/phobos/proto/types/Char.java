package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Created by lo on 1/11/17.
 */
public class Char extends ProtoType {
    Char(ProtoContext ctx, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
    }

    @Override
    public String name() {
        return "char";
    }
}
