package com.jkys.phobos.spring.client.beans;

import com.jkys.phobos.config.ClientConfig;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.config.RegistryConfig;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Created by lo on 1/6/17.
 */
public class ClientBean implements ApplicationListener<ContextRefreshedEvent> {
    public static final String NAME = "_phobosClientBean";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext appCtx = event.getApplicationContext();
        try {
            RegistryConfig registryConfig = (RegistryConfig) appCtx.getBean(RegistryConfig.NAME);
            PhobosConfig.getInstance().setRegistry(registryConfig);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
        try {
            ClientConfig clientConfig = (ClientConfig) appCtx.getBean(ClientConfig.NAME);
            PhobosConfig.getInstance().setClient(clientConfig);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
    }
}
