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
    private List<Object> elementStack = new ArrayList<>();

    public ProtoContext(Object element) {
        elementStack.add(element);
    }

    public void addObjectType(Type type) {
        types.add(type);
    }

    public Set<Type> getTypes() {
        return types;
    }

    public void pushElement(Object object) {
        elementStack.add(object);
    }

    public void popElement() {
        elementStack.remove(elementStack.size() - 1);
    }

    public String elements() {
        String[] eles = new String[elementStack.size()];
        for (int i = 0; i < eles.length; i++) {
            eles[i] = elementStack.get(i).toString();
        }
        return String.join(".", eles);
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
