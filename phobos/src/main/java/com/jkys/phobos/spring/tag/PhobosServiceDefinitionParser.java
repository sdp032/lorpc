package com.jkys.phobos.spring.tag;

import com.jkys.phobos.annotation.PhobosVersion;
import com.jkys.phobos.annotation.PhobosGroup;
import com.jkys.phobos.spring.server.PhobosContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.lang.reflect.Method;

/**
 * Created by zdj on 2016/7/4.
 */
public class PhobosServiceDefinitionParser implements BeanDefinitionParser{

    public BeanDefinition parse(Element element, ParserContext parserContext){

        String id = element.getAttribute("id");
        String className = element.getAttribute("class");

        Class<?> classObject = null;

        try {
            classObject = Class.forName(className);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            System.exit(0);
        }

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(classObject);
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);

        PhobosContext phobosContext = PhobosContext.getInstance();
        Method[] methods = classObject.getMethods();

        Class<?> interfaceClass = null;
        for(Class<?> c : classObject.getInterfaces()){
            if(c.getAnnotation(PhobosVersion.class)!=null||c.getAnnotation(PhobosGroup.class)!=null){
                interfaceClass = c;
                break;
            }else{
                for(Method method : c.getMethods()){
                    if(method.getAnnotation(PhobosVersion.class)!=null||method.getAnnotation(PhobosGroup.class)!=null){
                        interfaceClass = c;
                        break;
                    }
                }
            }
        }

        if(interfaceClass == null){
            new ClassCastException(classObject.getName() + "must implements phobos service interface").printStackTrace();
            System.exit(0);
        }

        for(Method method : methods){
            Method interfaceMethod = null;
            try{
                interfaceMethod = interfaceClass.getMethod(method.getName(),method.getParameterTypes());
            }catch (NoSuchMethodException e){
            }

            if(interfaceMethod != null){
                PhobosVersion version = interfaceMethod.getAnnotation(PhobosVersion.class) == null
                        ?interfaceClass.getAnnotation(PhobosVersion.class)
                        :interfaceMethod.getAnnotation(PhobosVersion.class);
                PhobosGroup group = interfaceMethod.getAnnotation(PhobosGroup.class) == null
                        ?interfaceClass.getAnnotation(PhobosGroup.class)
                        :interfaceMethod.getAnnotation(PhobosGroup.class);
                if(version == null){
                    new NullPointerException("Annotation PhobosVersion is null for " + interfaceMethod.getName()).printStackTrace();
                    System.exit(0);
                }
                if(group == null){
                    new NullPointerException("Annotation PhobosGroup is null for " + interfaceMethod.getName()).printStackTrace();
                    System.exit(0);
                }
                phobosContext.setMethodMap(interfaceClass.getName(),method.getName(),group.value(),version.version(),method);
            }

        }

        return beanDefinition;
    }
}
