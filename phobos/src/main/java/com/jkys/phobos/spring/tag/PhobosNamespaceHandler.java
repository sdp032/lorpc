package com.jkys.phobos.spring.tag;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by zdj on 2016/7/4.
 */
public class PhobosNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("registry", new RegistryDefinitionParser());
        registerBeanDefinitionParser("server", new ServerDefinitionParser());
        registerBeanDefinitionParser("provide", new ProviderDefinitionParser());
        registerBeanDefinitionParser("client", new ClientDefinitionParser());
        registerBeanDefinitionParser("consume", new ConsumerDefinitionParser());
    }
}
