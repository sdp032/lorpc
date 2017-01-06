package com.jkys.phobos.spring.tag;

import com.jkys.phobos.spring.server.ServerBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by zdj on 2016/7/4.
 */
public class ProviderDefinitionParser implements BeanDefinitionParser {

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String id = element.getAttribute("id");
        String className = element.getAttribute("class");

        Class<?> implClass = null;

        try {
            implClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        if (!parserContext.getRegistry().containsBeanDefinition(ServerBean.NAME)) {
            RootBeanDefinition serverBeanDef = new RootBeanDefinition();
            serverBeanDef.setBeanClass(ServerBean.class);
            parserContext.getRegistry().registerBeanDefinition(ServerBean.NAME, serverBeanDef);
        }
        ServerBean.register(implClass);

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(implClass);
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        return beanDefinition;
    }
}
