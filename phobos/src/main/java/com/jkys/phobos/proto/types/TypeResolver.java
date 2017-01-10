package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lo on 1/10/17.
 */
public class TypeResolver {
    private static Map<String, TypeBuilder> builders = new HashMap<>();
    private static TypeBuilder objBuilder = Obj::new;

    public static ProtoType resolve(ProtoContext ctx, Class<?> cls, AnnotatedElement ele) {
        String clsName = cls.getName();

        if (clsName.startsWith("[")) {
            return new Ary(ctx, cls, ele);
        } else {
            TypeBuilder builder = builders.get(cls.getName());
            if (builder != null) {
                return builder.build(ctx, cls, ele);
            }
            return objBuilder.build(ctx, cls, ele);
        }
    }

    static {
        builders.put("java.lang.String", Str::new);
        builders.put("boolean", Bool::new);
        builders.put("java.lang.Boolean", Bool::new);
        builders.put("int", (ctx, cls, ele) -> new Int(false, 32, ctx, cls,  ele));
        builders.put("java.lang.Integer", (ctx, cls, ele) -> new Int(false, 32, ctx, cls,  ele));
        builders.put("long", (ctx, cls, ele) -> new Int(false, 64, ctx, cls,  ele));
        builders.put("java.lang.Long", (ctx, cls, ele) -> new Int(false, 64, ctx, cls, ele));
        builders.put("float", Float32::new);
        builders.put("java.lang.Float", Float32::new);
        builders.put("double", Float32::new);
        builders.put("java.lang.Double", Float64::new);
        builders.put("byte", (ctx, cls, ele) -> new Int(false, 8, ctx, cls, ele));
        builders.put("java.lang.Byte", (ctx, cls, ele) -> new Int(false, 8, ctx, cls, ele));
        builders.put("java.util.ArrayList", Ary::new);
        builders.put("java.util.LinkedList", Ary::new);
        builders.put("java.util.HashMap", Dict::new);
        builders.put("java.util.LinkedHashMap", Dict::new);
        builders.put("java.util.TreeMap", Dict::new);
        builders.put("java.lang.Object", Any::new);
    }

    private interface TypeBuilder {
        ProtoType build(ProtoContext ctx, Class<?> cls, AnnotatedElement ele);
    }
}
