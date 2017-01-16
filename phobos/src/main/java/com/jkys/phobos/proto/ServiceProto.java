package com.jkys.phobos.proto;

import com.jkys.phobos.annotation.Rename;
import com.jkys.phobos.annotation.Service;
import com.jkys.phobos.annotation.ServiceUtil;
import com.jkys.phobos.proto.types.Obj;
import com.jkys.phobos.proto.types.ProtoType;
import com.jkys.phobos.proto.types.TypeResolver;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lo on 1/10/17.
// */
public class ServiceProto {
    private String serviceName;
    private String serviceVersion;
    private Class<?> interfaceClass;
    private Map<String, Function> functions = new ConcurrentHashMap<>();
    private Map<String, ProtoType> types = new HashMap<>();

    public ServiceProto(Class<?> cls) {
        if (!cls.isInterface()) {
            throw new RuntimeException("service class must be a interface");
        }
        this.interfaceClass = cls;
        Service serviceAnno = cls.getAnnotation(Service.class);
        if (serviceAnno == null || serviceAnno.value().equals("")) {
            throw new RuntimeException("missing Service annotation: " + cls.getTypeName());
        }
        String[] nameVersion = ServiceUtil.splitServiceKey(serviceAnno.value());
        serviceName = nameVersion[0];
        serviceVersion= nameVersion[1];

        ProtoContext ctx = new ProtoContext(cls);
        for (Method method : cls.getMethods()) {
            ctx.pushElement(method.getName());
            String name = method.getName();
            Rename rename = method.getAnnotation(Rename.class);
            if (rename != null && !rename.value().equals("")) {
                name = rename.value();
            }
            Function function = new Function(ctx, method);
            functions.put(name, function);
            ctx.popElement();
        }
        for (Type type : ctx.getTypes()) {
            ProtoType obj = Obj.get(ctx, type, null);
            types.put(obj.name(), obj);
        }
    }

    public Map<String, Object> dump() {
        Map<String, Object> typeDescs = new HashMap<>();
        for (ProtoType type : types.values()) {
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

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public Map<String, Function> getFunctions() {
        return functions;
    }

    public String toYaml() {
        Yaml yaml = new Yaml();
        return yaml.dump(dump());
    }
}
