package com.jkys.phobos.netty;

import com.jkys.phobos.annotation.ServiceUtil;
import com.jkys.phobos.client.ClientContext;
import com.jkys.phobos.client.ConnectionState;
import com.jkys.phobos.client.StateTracker;
import com.jkys.phobos.config.PhobosConfig;
import com.jkys.phobos.netty.codec.PhobosRequestEncoder;
import com.jkys.phobos.netty.codec.PhobosResponseDecoder;
import com.jkys.phobos.netty.handler.ClientHandler;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Created by frio on 16/7/4.
 */
public class ClientConnection implements ChannelFutureListener {
    private final Logger logger = LoggerFactory.getLogger(ClientConnection.class);
    public static final long RECONNECT_INTERVAL = 5;

    private String name;
    private String version;
    private String host;
    private int port;
    private volatile ChannelFuture future;
    private EventLoopGroup group;
    private StateTracker stateTracker;

    public ClientConnection(String name, String version, String host, int port, EventLoopGroup group) {
        this.name = name;
        this.version = version;
        this.host = host;
        this.port = port;
        this.group = group;
    }

    public void init(StateTracker stateTracker) {
        this.stateTracker = stateTracker;
    }

    public void connect() {
        synchronized (this) {
            if (this.stateTracker.isConnected()) {
                return;
            }
        }

        Integer timeout = PhobosConfig.getInstance().getClient().getResolveTimeout();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout * 1000)
                .handler(new ChannelInitializer<SocketChannel>() {
                     public void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new PhobosRequestEncoder());
                        socketChannel.pipeline().addLast(new PhobosResponseDecoder());
                        socketChannel.pipeline().addLast(new ClientHandler(ClientConnection.this));
                }
            });
        future = bootstrap.connect(host, port).addListener(this);
    }

    public PhobosResponse request(PhobosRequest request, long timeout, TimeUnit unit) throws InterruptedException {
        ClientContext.RequestPromise<PhobosResponse> promise = ClientContext.getInstance().newPromise();
        request.getHeader().setSequenceId(promise.getSequenceId());
        try {
            future.channel().writeAndFlush(request).await(PhobosConfig.getInstance().getClient().getRequestTimeout() * 1000);
        } catch (InterruptedException e) {
            // FIXME
            promise.cancel(false);
            throw new RuntimeException(e);
        }

        try {
            return promise.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // FIXME
            throw new RuntimeException(e);
        } finally {
            promise.cancel(false);
        }
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        synchronized (this) {
            boolean connected = future.isSuccess();
            if (connected) {
                this.stateTracker.changeState(ConnectionState.Ready);
                logger.info("connected to {}", this);
                this.notifyAll();
            } else {
                logger.warn("connect fail to {}", this);
            }
        }
        if (!future.isSuccess()) {
            future.channel().eventLoop().schedule(() -> {
                logger.info("reconnecting to {}", this);
                connect();
            }, RECONNECT_INTERVAL, TimeUnit.SECONDS);
        }
    }

    public void markDisconnected() {
        synchronized (this) {
            this.stateTracker.changeState(ConnectionState.Disconnected);
        }
    }

    public String toString() {
        return ServiceUtil.serviceKey(name, version) + "(" + host + ":" + port + ")";
    }
}
