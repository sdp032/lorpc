package com.jkys.phobos.internal;

import com.jkys.phobos.client.PhobosProxy;
import com.jkys.phobos.server.PhobosServer;
import com.jkys.phobos.server.Provider;
import com.jkys.phobos.server.ServerContext;
import com.jkys.phobos.util.LogUtil;
import io.netty.util.internal.ConcurrentSet;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lo on 1/20/17.
 */
public class PhobosInternalImpl implements PhobosInternal {
    private static ConcurrentHashMap<String, Provider> providers = new ConcurrentHashMap<>();
    private static ConcurrentSet<String> providerBeans = new ConcurrentSet<>();
    private static PhobosServer phobosServer = null;

    public InvocationHandler newClientProxy(Class<?> interfaceClass, String name, String version) {
        return new PhobosProxy(interfaceClass, name, version);
    }

    public void registryProvider(String bean) {
        if (!providerBeans.add(bean)) {
            // FIXME exception
            throw new RuntimeException("duplicated provider(bean): " + bean);
        }
    }

    public void registryProvider(Object impl) {
        Provider provider = new Provider(impl.getClass());
        provider.setImpl(impl);
        if (providers.putIfAbsent(provider.key(), provider) != null) {
            // FIXME exception
            throw new RuntimeException("duplicated provider: " + provider.key());
        }
    }

    public synchronized void triggerServer(ApplicationContext appCtx) {
        LogUtil.info("trigger phobos server");
        if (phobosServer != null) {
            return;
        }

        for (String bean: providerBeans) {
            Object impl = appCtx.getBean(bean);
            registryProvider(impl);
        }

        ServerContext serverContext = ServerContext.getInstance();
        for (Provider provider : providers.values()) {
            serverContext.register(provider);
        }

        phobosServer = new PhobosServer();
        phobosServer.start();
    }

    public void joinServer() throws InterruptedException {
        if (phobosServer != null) {
            phobosServer.join();
        }
    }

    public void stopServer(Long forceStopTimeoutInSeconds) {
        if (phobosServer != null) {
            phobosServer.stop(forceStopTimeoutInSeconds);
        }
    }
}
