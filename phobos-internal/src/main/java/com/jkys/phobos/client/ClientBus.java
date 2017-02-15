package com.jkys.phobos.client;

import com.github.infrmods.xbus.client.TLSInitException;
import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.item.ServiceEndpoint;
import com.jkys.phobos.annotation.ServiceUtil;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.exception.EnvException;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;
import com.jkys.phobos.util.RegistryUtil;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by lo on 1/7/17.
 */
public class ClientBus {
    private EventLoopGroup eventLoopGroup;
    private XBusClient xbus;
    private ConcurrentHashMap<String, Connections> connectionsMap = new ConcurrentHashMap<>();

    ClientBus() {
        eventLoopGroup = new NioEventLoopGroup();
        try {
            xbus = RegistryUtil.getXBus(PhobosConfig.getInstance().getRegistry());
        } catch (TLSInitException e) {
            throw new EnvException("get registry fail", e);
        }
    }

    public PhobosResponse request(PhobosRequest request, long timeout, TimeUnit unit) throws InterruptedException {
        Connections connections = getConnections(request.getRequest().getServiceName(),
                request.getRequest().getServiceVersion());
        return connections.request(request, timeout, unit);
    }

    private Connections getConnections(String name, String version) {
        String key = ServiceUtil.serviceKey(name, version);
        Connections connections = connectionsMap.get(key);
        if (connections != null) {
            return connections;
        }

        synchronized (this) {
            connections = connectionsMap.get(key);
            if (connections!= null) {
                return connections;
            }
            ServiceEndpoint[] presets = null;
            String presetAddress = PhobosConfig.getInstance().getClient().getPresetAddress(name, version);
            if (presetAddress != null) {
                presets = new ServiceEndpoint[]{new ServiceEndpoint(presetAddress, null)};
            }
            connections = new Connections(xbus, name, version,
                    presets, eventLoopGroup);
            connectionsMap.put(key, connections);
        }
        return connections;
    }
}
