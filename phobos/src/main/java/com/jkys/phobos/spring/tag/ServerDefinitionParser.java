package com.jkys.phobos.spring.tag;

import com.jkys.phobos.config.ServerConfig;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by lo on 1/6/17.
 */
public class ServerDefinitionParser implements BeanDefinitionParser {
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        ServerConfig config = new ServerConfig();
        if (element.hasAttribute("bindHost")) {
            config.setBindHost(element.getAttribute("bindHost"));
        }
        if (element.hasAttribute("bindPort")) {
            Integer port = Integer.valueOf(element.getAttribute("bindPort"));
            config.setBindPort(port);
        }

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(ServerConfig.class);
        ConstructorArgumentValues values = new ConstructorArgumentValues();
        values.addIndexedArgumentValue(0, config);
        beanDefinition.setConstructorArgumentValues(values);
        parserContext.getRegistry().registerBeanDefinition(ServerConfig.NAME, beanDefinition);
        return beanDefinition;
    }
}
