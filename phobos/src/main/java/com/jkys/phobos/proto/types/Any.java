package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by lo on 1/10/17.
 */
public class Any extends ProtoType {
    private static final String[] DEFAULT_TYPES = new String[]{
            "i8", "i16", "i32", "i64",
            "bool", "string", "float32", "float64", "bigint", "bytes"};
    private List<String> validTypes;

    Any(ProtoContext ctx, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
        validTypes = new ArrayList<>();
        Collections.addAll(validTypes, DEFAULT_TYPES);
    }

    @Override
    public String name() {
        return "any";
    }

    @Override
    public Map<String, Object> dump() {
        Map<String, Object> result = super.dump();
        result.put("types", validTypes);
        return result;
    }
}
