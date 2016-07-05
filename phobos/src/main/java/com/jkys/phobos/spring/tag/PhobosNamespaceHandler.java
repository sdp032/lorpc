package com.jkys.phobos.spring.tag;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by zdj on 2016/7/4.
 */
public class PhobosNamespaceHandler extends NamespaceHandlerSupport{

    public void init() {
        registerBeanDefinitionParser("registry" ,new PhobosRegistryDefinitionParser());
        registerBeanDefinitionParser("service" , new PhobosServiceDefinitionParser());
    }
}
