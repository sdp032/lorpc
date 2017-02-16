package com.jkys.phobos.client;

import com.jkys.phobos.annotation.ServiceUtil;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.codec.PhobosRequestEncoder;
import com.jkys.phobos.codec.PhobosResponseDecoder;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;
import com.jkys.phobos.util.Promise;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by frio on 16/7/4.
 */
public class ClientConnection implements ChannelFutureListener {
    private final Logger logger = LoggerFactory.getLogger(ClientConnection.class);
    static final long RECONNECT_INTERVAL = 5;

    private volatile boolean toClose = false;
    private volatile ChannelFuture future;
    private EventLoopGroup group;
    private StateTracker stateTracker;
    private AtomicLong reference = new AtomicLong(1);
    private ConcurrentHashMap<Long, Promise> relatedPromises = new ConcurrentHashMap<>();

    private String name;
    private String version;
    private String host;
    private int port;

    ClientConnection(String name, String version, String host, int port, EventLoopGroup group) {
        this.name = name;
        this.version = version;
        this.host = host;
        this.port = port;
        this.group = group;
    }

    void init(StateTracker stateTracker) {
        this.stateTracker = stateTracker;
    }

    void connect() {
        if (toClose || this.stateTracker.isConnected()) {
            return;
        }
        clearOldPromises();

        Integer timeout = PhobosConfig.getInstance().getClient().getResolveTimeout();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout * 1000)
                .handler(new ChannelInitializer<SocketChannel>() {
                     public void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new PhobosRequestEncoder());
                        socketChannel.pipeline().addLast(new PhobosResponseDecoder());
                        socketChannel.pipeline().addLast(new ClientChannelHandler(ClientConnection.this));
                }
            });
        future = bootstrap.connect(host, port).addListener(this);
    }

    void close() {
        toClose = true;
        deref();
    }

    public PhobosResponse request(PhobosRequest request, long timeout, TimeUnit unit) throws InterruptedException {
        ClientContext.RequestPromise<PhobosResponse> promise = ClientContext.getInstance().newPromise();
        request.getHeader().setSequenceId(promise.getSequenceId());
        relatedPromises.put(promise.getSequenceId(), promise);
        try {
            future.channel().writeAndFlush(request).await(PhobosConfig.getInstance().getClient().getRequestTimeout() * 1000);
        } catch (InterruptedException e) {
            // FIXME
            promise.cancel(false);
            relatedPromises.remove(promise.getSequenceId());
            throw new RuntimeException(e);
        }

        incref();
        try {
            return promise.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // FIXME
            throw new RuntimeException(e);
        } finally {
            relatedPromises.remove(promise.getSequenceId());
            deref();
            promise.cancel(false);
        }
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        boolean connected = future.isSuccess();
        if (connected) {
            logger.info("connected to {}", this);
            this.stateTracker.changeState(ConnectionState.Ready);
        } else {
            logger.warn("connect fail to {}", this);
            if (!toClose) {
                future.channel().eventLoop().schedule(() -> {
                    logger.info("reconnecting to {}", this);
                    connect();
                }, RECONNECT_INTERVAL, TimeUnit.SECONDS);
            }
        }
    }

    private void clearOldPromises() {
        synchronized (this) {
            for (Promise promise : relatedPromises.values()) {
                // TODO exception
                promise.setFailure(new RuntimeException("connection broken"));
            }
            relatedPromises.clear();
        }
    }

    void markDisconnected() {
        stateTracker.changeState(ConnectionState.Disconnected);
        clearOldPromises();
    }

    void markTemporaryShutdown() {
        stateTracker.changeState(ConnectionState.Unusable);
        deref();
    }

    private void incref() {
        reference.incrementAndGet();
    }

    private void deref() {
        long after = reference.decrementAndGet();
        if (after <= 0) {
            if (!toClose) {
                incref();
            }
            future.channel().close();
        }
    }

    public String toString() {
        return ServiceUtil.serviceKey(name, version) + "(" + host + ":" + port + ")";
    }
}
