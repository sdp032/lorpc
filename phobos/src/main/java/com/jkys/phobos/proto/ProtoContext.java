package com.jkys.phobos.proto;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lo on 1/10/17.
 */
public class ProtoContext {
    private Set<Type> types = new HashSet<>();
    private List<Type> objStack = new ArrayList<>();

    public void addObjectType(Type type) {
        types.add(type);
    }

    public Set<Type> getTypes() {
        return types;
    }

    public void enterObj(Type type) {
        if (objStack.indexOf(type) != -1) {
            throw new RuntimeException("type circular reference detected: " + type.getTypeName());
        }
        objStack.add(type);
    }

    public void popObj() {
        objStack.remove(objStack.size() - 1);
    }
}
