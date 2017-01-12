package com.jkys.phobos.proto;

import com.jkys.phobos.annotation.Rename;
import com.jkys.phobos.proto.types.Obj;
import com.jkys.phobos.proto.types.ProtoType;
import com.jkys.phobos.proto.types.TypeResolver;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lo on 1/10/17.
// */
public class ServiceProto {
    private Class<?> interfaceClass;
    private Map<String, Function> functions = new HashMap<>();
    private Map<String, Obj> types = new HashMap<>();

    public ServiceProto(Class<?> cls) {
        if (!cls.isInterface()) {
            throw new RuntimeException("service class must be a interface");
        }
        this.interfaceClass = cls;
        ProtoContext ctx = new ProtoContext();
        for (Method method : cls.getMethods()) {
            ProtoType returnType = TypeResolver.resolve(ctx, method.getGenericReturnType(), method);
            Parameter[] params = method.getParameters();
            Type[] paramTypes = method.getGenericParameterTypes();
            ProtoType[] protoTypes = new ProtoType[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                protoTypes[i] = TypeResolver.resolve(ctx, paramTypes[i], params[i]);
            }
            String name = method.getName();
            Rename rename = method.getAnnotation(Rename.class);
            if (rename != null && !rename.value().equals("")) {
                name = rename.value();
            }
            Function function = new Function(name, returnType, protoTypes);
            functions.put(name, function);
        }
        for (Type type : ctx.getTypes()) {
            Obj obj = new Obj(ctx, type, null);
            types.put(obj.name(), obj);
        }
    }

    public Map<String, Object> dump() {
        Map<String, Object> typeDescs = new HashMap<>();
        for (Obj type : types.values()) {
            typeDescs.put(type.name(), type.dumpObject());
        }
        Map<String, Object> functionDescs = new HashMap<>();
        for (Function func : functions.values()) {
            functionDescs.put(func.getName(), func.dump());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("types", typeDescs);
        result.put("service", functionDescs);
        return result;
    }

    public String toYaml() {
        Yaml yaml = new Yaml();
        return yaml.dump(dump());
    }
}
