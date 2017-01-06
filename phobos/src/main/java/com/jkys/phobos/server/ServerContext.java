package com.jkys.phobos.server;

import com.github.infrmods.xbus.item.ServiceDesc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lo on 1/6/17.
 */
public class ServerContext {
    private static ServerContext serverContext = new ServerContext();
    private ConcurrentHashMap<String, Provider> providers = new ConcurrentHashMap<>();

    public static ServerContext getInstance() {
        return serverContext;
    }

    public void register(Object instance) {
        register(new Provider(instance));
    }

    public void register(Provider provider) {
        if (providers.putIfAbsent(provider.key(), provider) != null) {
            throw new RuntimeException("duplicated provider: " + provider.key());
        }
    }

    public Provider getProvider(String serviceName, String serviceVersion) {
        return providers.get(serviceName + ":" + serviceVersion);
    }

    public ServiceDesc[] getServiceDescs() {
        List<ServiceDesc> descs = new ArrayList<>(providers.size());
        for (Provider provider : providers.values()) {
            descs.add(provider.getServiceDesc());
        }
        return descs.toArray(new ServiceDesc[providers.size()]);
    }
}
