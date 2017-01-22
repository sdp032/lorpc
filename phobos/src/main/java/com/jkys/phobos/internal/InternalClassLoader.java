package com.jkys.phobos.internal;

import com.jkys.phobos.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lo on 1/20/17.
 */
public class InternalClassLoader extends ClassLoader {
    private static final String RES_PREFIX = "service-kit/";
    private static final String IMPL_NAME = PhobosInternal.class.getName() + "Impl";

    InternalClassLoader() {
        this(InternalClassLoader.class.getClassLoader());
    }

    InternalClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                c = findClass(name);
            } catch (ClassNotFoundException e) {
                return super.loadClass(name, resolve);
            }
            if (resolve) {
                resolveClass(c);
            }
        }
        if (c == null) {
            throw new ClassNotFoundException();
        }
        return c;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/') + ".class";
        if (!name.equals(IMPL_NAME)) {
            path = RES_PREFIX + path;
        }
        InputStream classStream = getClass().getClassLoader().getResourceAsStream(path);
        if (classStream != null) {
            byte[] classData;
            try {
                classData = IOUtil.toByteArray(classStream);
            } catch (IOException e) {
                throw new RuntimeException("load class data fail", e);
            }
            return defineClass(name, classData, 0, classData.length);
        }
        return super.findClass(name);
    }
}
