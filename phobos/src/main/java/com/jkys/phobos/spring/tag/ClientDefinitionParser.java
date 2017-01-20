package com.jkys.phobos.spring.tag;

import com.jkys.phobos.serialization.SerializationType;
import com.jkys.phobos.config.ClientConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by lo on 1/6/17.
 */
public class ClientDefinitionParser implements BeanDefinitionParser{
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        ClientConfig config = ClientConfig.getInstance();
        if (element.hasAttribute("serialization")) {
            config.setSerializationType(SerializationType.valueOf(element.getAttribute("serialization")));
            if (config.getSerializationType() == null) {
                // FIXME
                throw new RuntimeException("invalid serialization: " + element.getAttribute("serialization"));
            }
        }
        if (element.hasAttribute("resolveTimeout")) {
            Integer timeout = Integer.valueOf(element.getAttribute("resolveTimeout"));
            config.setResolveTimeout(timeout);
        }
        if (element.hasAttribute("requestTimeout")) {
            Integer timeout = Integer.valueOf(element.getAttribute("requestTimeout"));
            config.setRequestTimeout(timeout);
        }

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(ClientConfig.class);
        beanDefinition.setFactoryMethodName("getInstance");
        parserContext.getRegistry().registerBeanDefinition(ClientConfig.NAME, beanDefinition);
        return beanDefinition;
    }
}
