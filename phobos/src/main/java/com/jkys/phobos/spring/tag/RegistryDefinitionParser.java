package com.jkys.phobos.spring.tag;

import com.jkys.phobos.config.RegistryConfig;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by zdj on 2016/7/4.
 * 解析自定义phobos registry标签
 */
public class RegistryDefinitionParser implements BeanDefinitionParser {
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        RegistryConfig config = new RegistryConfig();
        if (element.hasAttribute("endpoint")) {
            config.setEndpoint(element.getAttribute("endpoint"));
        }
        if (element.hasAttribute("keystorePath")) {
            config.setKeystorePath(element.getAttribute("keystorePath"));
        }
        if (element.hasAttribute("keystorePassword")) {
            config.setKeystorePassword(element.getAttribute("keystorePassword"));
        }

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(RegistryConfig.class);
        ConstructorArgumentValues values = new ConstructorArgumentValues();
        values.addIndexedArgumentValue(0, config);
        beanDefinition.setConstructorArgumentValues(values);
        parserContext.getRegistry().registerBeanDefinition(RegistryConfig.NAME, beanDefinition);
        return beanDefinition;
    }
}
