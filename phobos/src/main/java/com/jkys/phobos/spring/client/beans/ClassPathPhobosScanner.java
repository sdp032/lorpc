package com.jkys.phobos.spring.client.beans;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.util.Set;

/**
 * Created by zdj on 2016/6/30.
 * <p>
 * 自定义spring bean扫描器 用于扫描并注册phobos客户端
 */
public class ClassPathPhobosScanner extends ClassPathBeanDefinitionScanner {

    public ClassPathPhobosScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {

        }

        return null;
    }
}
