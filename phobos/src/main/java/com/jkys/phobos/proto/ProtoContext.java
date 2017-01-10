package com.jkys.phobos.proto;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lo on 1/10/17.
 */
public class ProtoContext {
    private Set<Class<?>> objectClasses = new HashSet<>();

    public void addObjectClass(Class<?> cls) {
        objectClasses.add(cls);
    }
}
