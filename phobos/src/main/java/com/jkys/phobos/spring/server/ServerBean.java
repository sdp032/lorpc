package com.jkys.phobos.spring.server;

import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.config.RegistryConfig;
import com.jkys.phobos.config.ServerConfig;
import com.jkys.phobos.netty.NettyServer;
import com.jkys.phobos.server.Provider;
import com.jkys.phobos.server.ServerContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lo on 1/6/17.
 */
public class ServerBean implements ApplicationListener<ContextRefreshedEvent> {
    public static final String NAME = "_phobosServerBean";
    private static NettyServer nettyServer = null;
    private static ConcurrentHashMap<String, Provider> providers = new ConcurrentHashMap<>();

    public static void register(Class<?> implClass) {
        Provider provider = new Provider(implClass);
        if (providers.putIfAbsent(provider.key(), provider) != null) {
            // FIXME exception
            throw new RuntimeException("duplicated provider: " + provider.key());
        }
    }

    private synchronized static void trigger(ApplicationContext appCtx) {
        if (nettyServer != null) {
            return;
        }

        try {
            RegistryConfig registryConfig = (RegistryConfig) appCtx.getBean(RegistryConfig.NAME);
            PhobosConfig.getInstance().setRegistry(registryConfig);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
        try {
            ServerConfig serverConfig = (ServerConfig) appCtx.getBean(ServerConfig.class);
            PhobosConfig.getInstance().setServer(serverConfig);
        } catch (NoSuchBeanDefinitionException ignored) {
        }
        ServerContext serverContext = ServerContext.getInstance();
        for (Provider provider : providers.values()) {
            Object impl = appCtx.getBean(provider.getImplClass());
            provider.setImpl(impl);
            serverContext.register(provider);
        }

        nettyServer = new NettyServer();
        nettyServer.noBlockOpen();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        trigger(event.getApplicationContext());
    }
}
