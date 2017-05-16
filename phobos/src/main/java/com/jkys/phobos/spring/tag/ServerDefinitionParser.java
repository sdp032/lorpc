package com.jkys.phobos.spring.tag;

import com.jkys.phobos.config.ServerConfig;
import org.springframework.beans.factory.config.BeanDefinition;
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
        ServerConfig config = ServerConfig.getInstance();
        if (element.hasAttribute("bindHost")) {
            config.setBindHost(element.getAttribute("bindHost"));
        }
        if (element.hasAttribute("bindPort")) {
            Integer port = Integer.valueOf(element.getAttribute("bindPort"));
            config.setBindPort(port);
        }
        if (element.hasAttribute("threads")) {
            Integer threads = Integer.valueOf(element.getAttribute("threads"));
            config.setThreads(threads);
        }

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(ServerConfig.class);
        beanDefinition.setFactoryMethodName("getInstance");
        parserContext.getRegistry().registerBeanDefinition(ServerConfig.NAME, beanDefinition);
        return beanDefinition;
    }
}
