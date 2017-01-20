package com.jkys.phobos.client;

import com.github.infrmods.xbus.client.TLSInitException;
import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.exceptions.NotFoundException;
import com.github.infrmods.xbus.exceptions.XBusException;
import com.github.infrmods.xbus.item.Service;
import com.github.infrmods.xbus.item.ServiceEndpoint;
import com.jkys.phobos.annotation.ServiceUtil;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.config.ServerConfig;
import com.jkys.phobos.exception.EnvException;
import com.jkys.phobos.netty.NettyClient;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;
import com.jkys.phobos.util.RegistryUtil;
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
    private ConcurrentHashMap<String, ServiceEndpoint[]> presetEndpoints = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<NettyClient>> serviceClients = new ConcurrentHashMap<>();

    ClientBus() {
        eventLoopGroup = new NioEventLoopGroup();
        try {
            xbus = RegistryUtil.getXBus(PhobosConfig.getInstance().getRegistry());
        } catch (TLSInitException e) {
            throw new EnvException("get registry fail", e);
        }
    }

    public void presetAddress(String name, String version, String address) {
        // TODO
        if (!address.contains(":")) {
            address = address + ":" + ServerConfig.DEFAULT_PORT;
        }
        ServiceEndpoint endpoint = new ServiceEndpoint(address, null);
        presetEndpoints.put(ServiceUtil.serviceKey(name, version), new ServiceEndpoint[]{endpoint});
    }

    public PhobosResponse request(PhobosRequest request, long timeout, TimeUnit unit) throws InterruptedException {
        List<NettyClient> clients = getClients(request.getRequest().getServiceName(),
                request.getRequest().getServiceVersion());
        if (clients == null || clients.size() == 0) {
            throw new RuntimeException("can't find endpoint of " +
                    ServiceUtil.serviceKey(
                            request.getRequest().getServiceName(),
                            request.getRequest().getServiceVersion()));
        }
        NettyClient client = clients.get(r.nextInt(clients.size()));
        return client.request(request, timeout, unit);
    }

    private List<NettyClient> getClients(String name, String version) {
        String key = ServiceUtil.serviceKey(name, version);
        List<NettyClient> clients = serviceClients.get(key);
        if (clients != null) {
            return clients;
        }

        ServiceEndpoint[] endpoints = presetEndpoints.get(key);
        if (endpoints == null) {
            Service service;
            try {
                service = xbus.getService(name, version);
            } catch (NotFoundException ignored) {
                return null;
            } catch (XBusException e) {
                // FIXME
                throw new RuntimeException(e);
            }
            endpoints = service.endpoints;
        }

        if (endpoints != null) {
            synchronized (this) {
                clients = serviceClients.get(key);
                if (clients != null) {
                    return clients;
                }
                clients = new ArrayList<>();
                for (ServiceEndpoint endpoint : endpoints) {
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
