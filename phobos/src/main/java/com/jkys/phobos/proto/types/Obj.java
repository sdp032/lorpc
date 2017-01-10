package com.jkys.phobos.proto.types;

import com.jkys.phobos.annotation.Rename;
import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lo on 1/10/17.
 */
public class Obj extends ProtoType {
    private static ConcurrentHashMap<Class<?>, List<ObjField>> fieldsMap = new ConcurrentHashMap<>();
    private String objName;
    private List<ObjField> fields = new ArrayList<>();

    Obj(ProtoContext ctx, Class<?> cls, AnnotatedElement ele) {
        super(ctx, cls, ele);
        objName = cls.getSimpleName();
        Rename rename = cls.getAnnotation(Rename.class);
        if (rename != null && !rename.value().equals("")) {
            objName = rename.value();
        }
        fields = makeFields(ctx, cls);
    }

    private static List<ObjField> makeFields(ProtoContext ctx, Class<?> cls) {
        ctx.addObjectClass(cls);
        List<ObjField> fields = fieldsMap.get(cls);
        if (fields == null) {
            fields = new ArrayList<>();
            for (Field field : cls.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                String name = field.getName();
                boolean isGetter = false;
                if (!Modifier.isPublic(field.getModifiers())) {
                    try {
                        String getterName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                        cls.getMethod(getterName);
                    } catch (NoSuchMethodException e) {
                        continue;
                    }
                    isGetter = true;
                }
                Rename fieldRename = field.getAnnotation(Rename.class);
                if (fieldRename != null && !fieldRename.value().equals("")) {
                    name = fieldRename.value();
                }
                fields.add(new ObjField(name, TypeResolver.resolve(ctx, field.getType(), field), isGetter));
            }
            fieldsMap.put(cls, fields);
        }
        return fields;
    }

    @Override
    public String name() {
        return objName;
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
