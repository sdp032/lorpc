package com.jkys.phobos.proto;

import com.jkys.phobos.annotation.Rename;
import com.jkys.phobos.proto.types.ProtoType;
import com.jkys.phobos.proto.types.TypeResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lo on 1/10/17.
 */
public class Function {
    private static Object[] objectArray = new Object[0];

    private String name;
    private Method method;
    private Type returnType;
    private Type paramsType;
    private Type[] paramTypes;
    private ProtoType returnProtoType;
    private ProtoType[] paramProtoTypes;

    public Function(ProtoContext ctx, Method method) {
        this.method = method;
        name = method.getName();
        Rename rename = method.getAnnotation(Rename.class);
        if (rename != null && !rename.value().equals("")) {
            name = rename.value();
        }

        returnType = method.getGenericReturnType();
        returnProtoType = TypeResolver.resolve(ctx, returnType, method);

        Parameter[] params = method.getParameters();
        paramTypes = method.getGenericParameterTypes();
        paramProtoTypes = new ProtoType[paramTypes.length];
        for (int i = 0; i < paramProtoTypes.length; i++) {
            paramProtoTypes[i] = TypeResolver.resolve(ctx, paramTypes[i], params[i]);
        }
        paramsType = new ParamsType(paramTypes);
    }

    public Map<String, Object> dump() {
        List<Map<String, Object>> paramDescs = new ArrayList<>(paramProtoTypes.length);
        for (ProtoType type : paramProtoTypes) {
            paramDescs.add(type.dump());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("params", paramDescs);
        result.put("return", returnProtoType.dump());
        return result;
    }

    public String getName() {
        return name;
    }

    public ProtoType getReturnProtoType() {
        return returnProtoType;
    }

    public ProtoType[] getParamProtoTypes() {
        return paramProtoTypes;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Type[] getParamTypes() {
        return paramTypes;
    }

    public Type getParamsType() {
        return paramsType;
    }

    public Method getMethod() {
        return method;
    }
}
