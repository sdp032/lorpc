package com.jkys.phobos.util.yaml;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

/**
 * Created by zdj on 2016/11/18.
 */
public class BeanRepresenter implements Representer <Class>{

    public String represent(Class aClass) throws NotFoundException {
        StringBuffer sb = new StringBuffer();

        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(aClass.getName());

        String name = aClass.getName();

        Method[] methods = aClass.getDeclaredMethods();

        sb.append(name);
        sb.append(":\n");

        if (aClass.getDeclaredFields().length>0){
            sb.append("\tfields:\n");
            for (Field field : aClass.getDeclaredFields()){
                sb.append("\t  -\n\t\tname: ");
                sb.append(field.getName());
                sb.append("\n\t\ttype: ");
                sb.append(field.getType().getName());
                sb.append("\n\t\tnullable: true\n");
                if(field.getGenericType() instanceof ParameterizedType){
                    if(((ParameterizedType) field.getGenericType()).getActualTypeArguments().length == 1){
                        sb.append("\t\titem_type: ");
                        sb.append(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
                        sb.append("\n\t\titem_nullable: true\n");
                    }else if(((ParameterizedType) field.getGenericType()).getActualTypeArguments().length == 2){
                        sb.append("\t\tkey_type: ");
                        sb.append(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
                        sb.append("\n\t\tvalue_type: ");
                        sb.append(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1].getTypeName());
                        sb.append("\n\t\tvalue_nullable: true\n");
                    }

                }
            }
        }

        for (Method method : methods){
            sb.append("\t");
            sb.append(method.getName());
            sb.append(":\n");

            if(!"void".equals(method.getReturnType().getName())){
                sb.append("\t\treturn:\n\t\t\ttype: ");
                sb.append(method.getReturnType().getName());
                sb.append("\n\t\t\tnullable: true\n");
            }

            if(method.getParameterTypes().length>0){
                CtClass[] params = new CtClass[method.getParameterTypes().length];
                for (int i = 0; i < method.getParameterTypes().length; i++){
                    params[i] = pool.getCtClass(method.getParameterTypes()[i].getName());
                }
                CtMethod cm = cc.getDeclaredMethod(method.getName(), params);
                MethodInfo methodInfo = cm.getMethodInfo();
                CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

                if(attr != null){
                    int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
                    sb.append("\t\tparams:\n");
                    for(int i = 0; i < cm.getParameterTypes().length; i++){
                        sb.append("\t\t  -\n\t\t\tname: ");
                        sb.append(attr.variableName(i + pos));
                        sb.append("\n\t\t\ttype: ");
                        sb.append(method.getGenericParameterTypes()[i].getTypeName());
                       // sb.append(method.getParameterTypes()[i].getName());
                        sb.append("\n\t\t\tnullable: false\n");
                    }
                }
            }

            if(method.getExceptionTypes().length>0){
                sb.append("\t\texceptions:\n");
                for(Class eClass : method.getExceptionTypes()){
                    sb.append("\t\t  -\n\t\t\t");
                    sb.append(eClass.getName());
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
}
