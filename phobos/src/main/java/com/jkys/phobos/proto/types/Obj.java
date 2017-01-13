package com.jkys.phobos.proto.types;

import com.jkys.phobos.annotation.Rename;
import com.jkys.phobos.proto.ParameterizedTypeImpl;
import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lo on 1/10/17.
 */
public class Obj extends ProtoType {
    private static ConcurrentHashMap<Type, List<ObjField>> fieldsMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Type, Map<TypeVariable, Type>> cachedVariables = new ConcurrentHashMap<>();

    private String objName;
    private List<ObjField> fields = new ArrayList<>();

    public Obj(ProtoContext ctx, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
        ctx.enterObj(type);
        objName = type.getTypeName();
        Class<?> rawType;
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            rawType = (Class<?>) ptype.getRawType();
        } else if (type instanceof Class) {
            rawType = (Class<?>) type;
            if (rawType.isInterface()) {
                throw new RuntimeException("can't use interface in rpc proto");
            }
        } else {
            throw  new RuntimeException("unhandled type: " + type.getTypeName());
        }
        if (Modifier.isAbstract(rawType.getModifiers())) {
            // FIXME
            throw new RuntimeException("can't use abstract class");
        }
        Rename rename = rawType.getAnnotation(Rename.class);
        if (rename != null && !rename.value().equals("")) {
            objName = rename.value();
        }
        fields = makeFields(ctx, type);
        ctx.popObj();
    }

    public List<ObjField> getFields() {
        return fields;
    }

    private static List<ObjField> makeFields(ProtoContext ctx, Type type) {
        List<ObjField> fields = fieldsMap.get(type);
        if (fields == null) {
            ctx.addObjectType(type);
            Class<?> rawType;
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                rawType = (Class<?>) ptype.getRawType();
            } else {
                rawType = (Class<?>) type;
            }
            fields = new ArrayList<>();
            for (Field field : getRawFields(rawType)) {
                String name = field.getName();
                Rename fieldRename = field.getAnnotation(Rename.class);
                if (fieldRename != null && !fieldRename.value().equals("")) {
                    name = fieldRename.value();
                }
                String reprName = type.getTypeName() + "#" + field.getName();
                Type fieldType = cleanFieldType(type, reprName, field.getGenericType());
                boolean isGetter = !Modifier.isPublic(field.getModifiers());
                fields.add(new ObjField(name, TypeResolver.resolve(ctx, fieldType, field), isGetter));
            }
            fieldsMap.put(type, fields);
        }
        return fields;
    }

    private static List<Field> getRawFields(Class<?> cls) {
        // TODO cache
        List<Field> fields;
        Class<?> superClass = cls.getSuperclass();
        if (superClass == null) {
            fields = new LinkedList<>();
        } else {
            fields = getRawFields(superClass);
        }
        for (Field field : cls.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers())) {
                fields.add(field);
            } else {
                String fieldName = field.getName();
                String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                Method method;
                try {
                    method = cls.getMethod(getterName);
                } catch (NoSuchMethodException e) {
                    try {
                        method = cls.getMethod(fieldName);
                    } catch (NoSuchMethodException e1) {
                        continue;
                    }
                }
                if (Modifier.isPublic(method.getModifiers()) &&
                        method.getGenericReturnType().equals(field.getGenericType())) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    public static Type cleanFieldType(Type ownerType, String name, Type type) {
        if (type instanceof Class) {
            return type;
        }
        if (type instanceof TypeVariable) {
            Type actualType = getTypeVariableActualType(ownerType, (TypeVariable) type);
            if (actualType == null) {
                throw new RuntimeException("can't detect TypeVariable: " + name);
            }
            return actualType;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType paramedType = (ParameterizedType) type;
            Type origArgs[] = paramedType.getActualTypeArguments();
            boolean converted = false;
            Type args[] = new Type[origArgs.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = origArgs[i];
                if (origArgs[i] instanceof Class) {
                    continue;
                }
                args[i] = cleanFieldType(ownerType, name, origArgs[i]);
                converted = true;
            }
            if (converted) {
                return new ParameterizedTypeImpl(paramedType.getRawType(), args);
            }
            return paramedType;
        }
        throw new RuntimeException("unhandled field type: " + name);
    }

    private static Type getTypeVariableActualType(Type owner, TypeVariable var) {
        Map<TypeVariable, Type> variables = cachedVariables.get(owner);
        if (variables == null) {
            variables = collectVariableTypes(null, owner);
            cachedVariables.put(owner, variables);
        }
        Type type = variables.get(var);
        while (type != null && type instanceof TypeVariable) {
            type = variables.get(type);
        }
        return type;
    }

    private static Map<TypeVariable, Type> collectVariableTypes(Map<TypeVariable, Type> m, Type t) {
        if (m == null) {
            m = new HashMap<>();
        }
        if (t instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) t;
            Class cls = (Class) ptype.getRawType();
            TypeVariable vars[] = cls.getTypeParameters();
            for (int i = 0; i< vars.length; i++) {
                m.put(vars[i], ptype.getActualTypeArguments()[i]);
            }
            m = collectVariableTypes(m, cls.getGenericSuperclass());
        } else if (t instanceof Class) {
            m = collectVariableTypes(m, ((Class) t).getGenericSuperclass());
        }
        return m;
    }

    @Override
    public String name() {
        return objName;
    }

    public Map<String, Object> dumpObject() {
        List<Map<String, Object>> fieldDescs = new ArrayList<>(fields.size());
        for (ObjField field : fields) {
            Map<String, Object> desc = field.fieldType.dump();
            desc.put("name", field.fieldName);
            fieldDescs.add(desc);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("fields", fieldDescs);
        return result;
    }

    public static class ObjField {
        public String fieldName;
        public ProtoType fieldType;
        public boolean isGetter;

        public ObjField(String fieldName, ProtoType fieldType, boolean isGetter) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.isGetter = isGetter;
        }
    }
}
