package com.jkys.phobos.util.yaml;

import com.jkys.phobos.annotation.Param;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * Created by zdj on 2016/11/18.
 */
public class BeanRepresenter implements Representer<Class> {

    public String represent(Class aClass) {

        StringBuffer sb = new StringBuffer();
        String name = aClass.getName();
        Method[] methods = aClass.getDeclaredMethods();

        sb.append(name);
        sb.append(":\n");

        if (aClass.getDeclaredFields().length > 0) {
            sb.append("\tfields:\n");
            for (Field field : aClass.getDeclaredFields()) {
                sb.append("\t  -\n\t\tname: ");
                sb.append(field.getName());
                sb.append("\n\t\ttype: ");
                sb.append(field.getType().getName());
                sb.append("\n\t\tnullable: true\n");
                if (field.getGenericType() instanceof ParameterizedType) {
                    if (((ParameterizedType) field.getGenericType()).getActualTypeArguments().length == 1) {
                        sb.append("\t\titem_type: ");
                        sb.append(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].toString());
                        sb.append("\n\t\titem_nullable: true\n");
                    } else if (((ParameterizedType) field.getGenericType()).getActualTypeArguments().length == 2) {
                        sb.append("\t\tkey_type: ");
                        sb.append(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].toString());
                        sb.append("\n\t\tvalue_type: ");
                        sb.append(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1].toString());
                        sb.append("\n\t\tvalue_nullable: true\n");
                    }

                }
            }
        }

        for (Method method : methods) {
            sb.append("\t");
            sb.append(method.getName());
            sb.append(":\n");

            if (!"void".equals(method.getReturnType().getName())) {
                sb.append("\t\treturn:\n\t\t\ttype: ");
                sb.append(method.getReturnType().getName());
                sb.append("\n\t\t\tnullable: true\n");
            }

            Annotation[][] annotations = method.getParameterAnnotations();
            if (method.getParameterTypes().length > 0) sb.append("\t\tparams:\n");
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                sb.append("\t\t  -\n");
                for (Annotation annotation : annotations[i]) {
                    if (annotation instanceof Param) {
                        sb.append("\t\t\tname: ");
                        sb.append(((Param) annotation).name());
                        sb.append("\n");
                    }
                }
                sb.append("\t\t\ttype: ");
                sb.append(method.getParameterTypes()[i].getName());
                sb.append("\n");
            }

            if (method.getExceptionTypes().length > 0) {
                sb.append("\t\texceptions:\n");
                for (Class eClass : method.getExceptionTypes()) {
                    sb.append("\t\t  -\n\t\t\t");
                    sb.append(eClass.getName());
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
}
