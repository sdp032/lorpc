package com.jkys.phobos.internal;

import com.jkys.phobos.client.PhobosProxy;
import com.jkys.phobos.netty.NettyServer;
import com.jkys.phobos.server.Provider;
import com.jkys.phobos.server.ServerContext;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lo on 1/20/17.
 */
public class PhobosInternalImpl implements PhobosInternal {
    private static ConcurrentHashMap<String, Provider> providers = new ConcurrentHashMap<>();
    private static NettyServer nettyServer = null;
    private Thread serverThread = null;

    public InvocationHandler newClientProxy(Class<?> interfaceClass, String name, String version) {
        return new PhobosProxy(interfaceClass, name, version);
    }

    public void registryProvider(Class<?> implClass) {
        Provider provider = new Provider(implClass);
        if (providers.putIfAbsent(provider.key(), provider) != null) {
            // FIXME exception
            throw new RuntimeException("duplicated provider: " + provider.key());
        }
    }

    public synchronized void triggerServer(ApplicationContext appCtx) {
        if (nettyServer != null) {
            return;
        }

        ServerContext serverContext = ServerContext.getInstance();
        for (Provider provider : providers.values()) {
            Object impl = appCtx.getBean(provider.getImplClass());
            provider.setImpl(impl);
            serverContext.register(provider);
        }

        nettyServer = new NettyServer();
        serverThread = nettyServer.noBlockOpen();
    }

    public void joinServer() throws InterruptedException {
        serverThread.join();
    }
}
