package com.jkys.phobos.proto.types;

import com.jkys.phobos.annotation.Rename;
import com.jkys.phobos.exception.TypeUnsupported;
import com.jkys.phobos.proto.ParameterizedTypeImpl;
import com.jkys.phobos.proto.ProtoContext;

import java.lang.*;
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

    private Obj(ProtoContext ctx, Class<?> rawType, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
        ctx.addObjectType(type);
        ctx.enterObj(type);
        if (Modifier.isAbstract(rawType.getModifiers())) {
            throw new TypeUnsupported(type, "can't use abstract class: " + ctx.elements());
        }
        objName = type.getTypeName();
        Rename rename = rawType.getAnnotation(Rename.class);
        if (rename != null && !rename.value().equals("")) {
            objName = rename.value();
        }
        ctx.pushElement("(" + type.getTypeName() + ")");
        fields = makeFields(ctx, type);
        ctx.popElement();
        ctx.popObj();
    }

    public static ProtoType get(ProtoContext ctx, Type type, AnnotatedElement ele) {
        Class<?> rawType;
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType) type;
            rawType = (Class<?>) ptype.getRawType();
        } else if (type instanceof Class) {
            rawType = (Class<?>) type;
            if (rawType.isInterface()) {
                throw new TypeUnsupported(type, "can't use interface in rpc proto:" + ctx.elements());
            }
        } else {
            throw new TypeUnsupported(type, "unhandled type: " + ctx.elements());
        }
        if (rawType.isEnum()) {
            return new com.jkys.phobos.proto.types.Enum(ctx, rawType, type, ele);
        }
        return new Obj(ctx, rawType, type, ele);
    }

    public List<ObjField> getFields() {
        return fields;
    }

    private static List<ObjField> makeFields(ProtoContext ctx, Type type) {
        List<ObjField> fields = fieldsMap.get(type);
        if (fields == null) {
            Class<?> rawType;
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                rawType = (Class<?>) ptype.getRawType();
            } else {
                rawType = (Class<?>) type;
            }
            fields = new ArrayList<>();
            for (Field field : getRawFields(rawType)) {
                ctx.pushElement(field.getName());
                String name = field.getName();
                Rename fieldRename = field.getAnnotation(Rename.class);
                if (fieldRename != null && !fieldRename.value().equals("")) {
                    name = fieldRename.value();
                }
                Type fieldType = cleanFieldType(ctx, type, field.getGenericType());
                boolean isGetter = !Modifier.isPublic(field.getModifiers());
                fields.add(new ObjField(name, TypeResolver.resolve(ctx, fieldType, field), isGetter));
                ctx.popElement();
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

    private static Type cleanFieldType(ProtoContext ctx, Type ownerType, Type type) {
        if (type instanceof Class) {
            return type;
        }
        if (type instanceof TypeVariable) {
            Type actualType = getTypeVariableActualType(ownerType, (TypeVariable) type);
            if (actualType == null) {
                throw new TypeUnsupported(type, "can't detect TypeVariable: " + ctx.elements());
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
                args[i] = cleanFieldType(ctx, ownerType, origArgs[i]);
                converted = true;
            }
            if (converted) {
                return new ParameterizedTypeImpl(paramedType.getRawType(), args);
            }
            return paramedType;
        }
        throw new TypeUnsupported(type, "unhandled field type: " + ctx.elements());
    }

    private static Type getTypeVariableActualType(Type owner, TypeVariable var) {
        Map<TypeVariable, Type> variables = cachedVariables.computeIfAbsent(owner, k -> collectVariableTypes(null, owner));
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

    @Override
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
        String fieldName;
        ProtoType fieldType;
        boolean isGetter;

        ObjField(String fieldName, ProtoType fieldType, boolean isGetter) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.isGetter = isGetter;
        }
    }
}
