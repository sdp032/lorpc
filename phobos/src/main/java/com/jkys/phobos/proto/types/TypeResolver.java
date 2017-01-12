package com.jkys.phobos.proto.types;

import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lo on 1/10/17.
 */
public class TypeResolver {
    private static Map<String, TypeBuilder> builders = new HashMap<>();
    private static TypeBuilder objBuilder = Obj::new;

    public static ProtoType resolve(ProtoContext ctx, Type type, AnnotatedElement ele) {
        String clsName = type.getTypeName();
        if (clsName.contains("<")) {
            clsName = clsName.substring(0, clsName.indexOf("<"));
        }

        if (clsName.endsWith("]") || (type instanceof GenericArrayType)) {
            return new Ary(ctx, type, ele);
        } else {
            TypeBuilder builder = builders.get(clsName);
            if (builder != null) {
                return builder.build(ctx, type, ele);
            }
            return objBuilder.build(ctx, type, ele);
        }
    }

    static {
        builders.put("java.lang.String", Str::new);
        builders.put("boolean", Bool::new);
        builders.put("java.lang.Boolean", Bool::new);
        builders.put("int", (ctx, type, ele) -> new Int(false, 32, ctx, type,  ele));
        builders.put("java.lang.Integer", (ctx, type, ele) -> new Int(false, 32, ctx, type,  ele));
        builders.put("short", (ctx, type, ele) -> new Int(false, 16, ctx, type,  ele));
        builders.put("java.lang.Short", (ctx, type, ele) -> new Int(false, 16, ctx, type,  ele));
        builders.put("long", (ctx, type, ele) -> new Int(false, 64, ctx, type,  ele));
        builders.put("java.lang.Long", (ctx, type, ele) -> new Int(false, 64, ctx, type, ele));
        builders.put("java.math.BigInteger", BigInt::new);
        builders.put("float", Float32::new);
        builders.put("java.lang.Float", Float32::new);
        builders.put("double", Float32::new);
        builders.put("java.lang.Double", Float64::new);
        builders.put("byte", (ctx, type, ele) -> new Int(false, 8, ctx, type, ele));
        builders.put("java.lang.Byte", (ctx, type, ele) -> new Int(false, 8, ctx, type, ele));
        builders.put("char", Char::new);
        builders.put("java.lang.Character", Char::new);
        builders.put("java.util.List", Ary::new);
        builders.put("java.util.ArrayList", Ary::new);
        builders.put("java.util.LinkedList", Ary::new);
        builders.put("java.util.Map", Dict::new);
        builders.put("java.util.HashMap", Dict::new);
        builders.put("java.util.LinkedHashMap", Dict::new);
        builders.put("java.util.TreeMap", Dict::new);
        builders.put("java.lang.Object", Any::new);
    }

    private interface TypeBuilder {
        ProtoType build(ProtoContext ctx, Type type, AnnotatedElement ele);
    }
}
