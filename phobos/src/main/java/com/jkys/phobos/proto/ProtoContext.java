package com.jkys.phobos.proto;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lo on 1/10/17.
 */
public class ProtoContext {
    private Set<Type> objectClasses = new HashSet<>();

    public void addObjectClass(Type type) {
        objectClasses.add(type);
    }
}
