package com.jkys.phobos.proto.types;

import com.jkys.phobos.annotation.Rename;
import com.jkys.phobos.proto.ProtoContext;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lo on 1/16/17.
 */
public class Enum extends ProtoType {
    private String enumName;
    private List<String> items;

    Enum(ProtoContext ctx, Class<?> rawType, Type type, AnnotatedElement ele) {
        super(ctx, type, ele);
        if (!rawType.isEnum()) {
            throw new RuntimeException("invalid enum: " + ctx.elements());
        }
        ctx.addObjectType(type);
        enumName = type.getTypeName();
        Rename typeRename = rawType.getAnnotation(Rename.class);
        if (typeRename != null && !typeRename.equals("")) {
            enumName = typeRename.value();
        }

        items = new ArrayList<>();
        for (Field field : rawType.getFields()) {
            String name = field.getName();
            Rename rename = field.getAnnotation(Rename.class);
            if (rename != null && !rename.equals("")) {
                name = rename.value();
            }
            items.add(name);
        }
    }

    @Override
    public Map<String, Object> dumpObject() {
        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        return result;
    }

    @Override
    public String name() {
        return enumName;
    }
}
