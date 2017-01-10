package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Created by lo on 1/10/17.
 */
public class Int extends ProtoType {
    private boolean unsigned;
    private int size;

    public Int(boolean signed, int size, ProtoContext ctx, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
        this.unsigned = signed;
        this.size = size;
    }

    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String name() {
        if (unsigned) {
            return "i" + size;
        }
        return "u" + size;
    }
}
