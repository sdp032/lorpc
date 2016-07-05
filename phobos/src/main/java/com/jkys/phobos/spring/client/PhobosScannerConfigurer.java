package com.jkys.phobos.spring.client;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.lang.annotation.Annotation;

/**
 * Created by zdj on 2016/6/30.
 *
 * 该类需配置在spring容器内，spring容器启动时通过该类将指定包（含子包）下的phobos客户端注册到spring容器
 */
public class PhobosScannerConfigurer implements BeanDefinitionRegistryPostProcessor{

    private String basePackage;

    private Class<? extends Annotation> annotationClass;

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {


    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }
}
