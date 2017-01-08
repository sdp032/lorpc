package com.jkys.phobos.client;

import com.github.infrmods.xbus.client.TLSInitException;
import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.exceptions.NotFoundException;
import com.github.infrmods.xbus.exceptions.XBusException;
import com.github.infrmods.xbus.item.Service;
import com.github.infrmods.xbus.item.ServiceEndpoint;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.netty.NettyClient;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by lo on 1/7/17.
 */
public class ClientBus {
    private Random r = new Random();
    private EventLoopGroup eventLoopGroup;
    private XBusClient xbus;
    private ConcurrentHashMap<String, List<NettyClient>> serviceClients = new ConcurrentHashMap<>();

    ClientBus() {
        eventLoopGroup = new NioEventLoopGroup();
        try {
            xbus = PhobosConfig.getInstance().getRegistry().getXBus();
        } catch (TLSInitException e) {
            // FIXME
            throw new RuntimeException(e);
        }
    }

    public PhobosResponse request(PhobosRequest request, long timeout, TimeUnit unit) throws InterruptedException {
        List<NettyClient> clients = getClients(request.getRequest().getServiceName(),
                request.getRequest().getServiceVersion());
        if (clients == null || clients.size() == 0) {
            throw new RuntimeException("can't find endpoint of " +
                    request.getRequest().getServiceName() + ":" +
                    request.getRequest().getServiceVersion());
        }
        NettyClient client = clients.get(r.nextInt(clients.size()));
        return client.request(request, timeout, unit);
    }

    private List<NettyClient> getClients(String name, String version) {
        String key = name + ":" + version;
        List<NettyClient> clients = serviceClients.get(key);
        if (clients != null) {
            return clients;
        }

        Service service;
        try {
            service = xbus.getService(name, version);
        } catch (NotFoundException ignored) {
            return null;
        } catch (XBusException e) {
            // FIXME
            throw new RuntimeException(e);
        }

        if (service.endpoints != null) {
            synchronized (this) {
                clients = serviceClients.get(key);
                if (clients != null) {
                    return clients;
                }
                clients = new ArrayList<>();
                for (ServiceEndpoint endpoint : service.endpoints) {
                    NettyClient client = new NettyClient(key,
                            endpoint.getHost(), endpoint.getPort(), eventLoopGroup);
                    client.connect();
                    clients.add(client);
                }
                serviceClients.put(key, clients);
            }
        }
        return clients;
    }
}
