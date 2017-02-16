package com.jkys.phobos.client;

import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.exceptions.XBusException;
import com.github.infrmods.xbus.item.Service;
import com.github.infrmods.xbus.item.ServiceEndpoint;
import com.jkys.phobos.exception.ConnectTimeoutException;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;
import io.netty.channel.EventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by lo on 2/8/17.
 */
class Connections {
    private final Logger logger = LoggerFactory.getLogger(Connections.class);

    private Random r = new Random();
    private XBusClient xBusClient;
    private String serviceName;
    private String serviceVersion;
    private EventLoopGroup eventLoopGroup;

    private ReadWriteLock connsLock = new ReentrantReadWriteLock();
    private Condition connsCond = connsLock.writeLock().newCondition();
    private ServiceEndpoint[] endpoints;
    private Map<String, ConnState> connections = new HashMap<>();
    private List<ConnState> readyConnections = new ArrayList<>();

    Connections(XBusClient xBusClient, String name, String version,
                ServiceEndpoint[] presetEndpoints, EventLoopGroup eventLoopGroup) {
        this.xBusClient = xBusClient;
        this.serviceName = name;
        this.serviceVersion = version;
        this.endpoints = presetEndpoints;
        this.eventLoopGroup = eventLoopGroup;
        this.initConnections();
    }

    public PhobosResponse request(PhobosRequest request, long timeout, TimeUnit unit) throws InterruptedException {
        // TODO use resolve timeout
        ClientConnection connection = peekConnection(timeout, unit);
        return connection.request(request, timeout, unit);
    }

    private ClientConnection peekConnection(long timeout, TimeUnit unit) {
        while (true) {
            connsLock.readLock().lock();
            try {
                if (readyConnections.size() > 0) {
                    return readyConnections.get(r.nextInt(readyConnections.size())).conn;
                }
            } finally {
                connsLock.readLock().unlock();
            }
            if (timeout <= 0) {
                throw new ConnectTimeoutException();
            }
            timeout -= waitConnReady(timeout, unit);
        }
    }

    private long waitConnReady(long timeout, TimeUnit unit) {
        long start;
        connsLock.writeLock().lock();
        try {
            if (readyConnections.size() > 0) {
                return 0;
            }
            start = System.currentTimeMillis();
            if (!connsCond.await(timeout, unit)) {
                throw new ConnectTimeoutException();
            }
        } catch (InterruptedException e) {
            // TODO fixme
            throw new RuntimeException(e);
        } finally {
            connsLock.writeLock().unlock();
        }
        return unit.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
    }

    private void initConnections() {
        if (endpoints == null) {
            try {
                Service service = xBusClient.getService(serviceName, serviceVersion);
                endpoints = service.endpoints;
            } catch (XBusException e) {
                // FIXME
                throw new RuntimeException(e);
            }
            // TODO shutdown watch
            new Thread(() -> {
                onEndpointsChange(endpoints);
                while (true) {
                    try {
                        Service service = xBusClient.watchService(serviceName, serviceVersion);
                        onEndpointsChange(service.endpoints);
                    } catch (XBusException e) {
                        e.printStackTrace();
                        // FIXME const
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            return;
                        }
                    }
                }
            }).start();
        } else {
            onEndpointsChange(endpoints);
        }
    }

    private void onEndpointsChange(ServiceEndpoint[] endpoints) {
        connsLock.writeLock().lock();
        try {
            Set<String> toRemove = new HashSet<>(connections.keySet());
            List<ServiceEndpoint> toAdd = new ArrayList<>();
            for (ServiceEndpoint endpoint : endpoints) {
                if (!toRemove.remove(endpoint.address)) {
                    toAdd.add(endpoint);
                }
            }
            for (String addr : toRemove) {
                ConnState connState = connections.remove(addr);
                connState.changeState(ConnectionState.Unusable);
                // TODO close connection
            }
            for (ServiceEndpoint endpoint : toAdd) {
                ClientConnection conn = new ClientConnection(serviceName, serviceVersion ,
                        endpoint.getHost(), endpoint.getPort(), eventLoopGroup);
                ConnState connState = new ConnState(endpoint.address, conn);
                conn.init(connState);
                conn.connect();
                connections.put(endpoint.address, connState);
            }
            this.endpoints = endpoints;
            if (toAdd.size() > 0 || toRemove.size() > 0) {
                makeReadyConnections();
            }
        } finally {
            connsLock.writeLock().unlock();
        }
    }

    private void makeReadyConnections() {
        int oldSize = readyConnections.size();
        readyConnections.clear();
        for (ConnState connState : connections.values()) {
            if (ConnectionState.Ready.equals(connState.getState())) {
                readyConnections.add(connState);
            }
        }
        if (oldSize == 0 && readyConnections.size() > 0) {
            connsCond.signalAll();
        }
    }

    class ConnState implements StateTracker {
        volatile ConnectionState state = ConnectionState.Disconnected;
        String key;
        ClientConnection conn;

        ConnState(String key, ClientConnection conn) {
            this.key = key;
            this.conn = conn;
        }

        @Override
        public void changeState(ConnectionState state) {
            Connections.this.connsLock.writeLock().lock();
            try {
                if (Connections.this.connections.containsKey(key)) {
                    this.state = state;
                    Connections.this.makeReadyConnections();
                } else {
                    conn.close();
                }
            } finally {
                Connections.this.connsLock.writeLock().unlock();
            }
        }

        @Override
        public ConnectionState getState() {
            return state;
        }

        @Override
        public boolean isConnected() {
            return !ConnectionState.Disconnected.equals(state);
        }
    }
}
