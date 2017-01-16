package com.jkys.phobos.netty;

import com.jkys.phobos.client.ClientContext;
import com.jkys.phobos.config.PhobosConfig;
//import com.jkys.phobos.netty.channel.HeartBeatPingChannelHandler;
import com.jkys.phobos.netty.codec.PhobosRequestEncoder;
import com.jkys.phobos.netty.codec.PhobosResponseDecoder;
import com.jkys.phobos.netty.handler.ClientHandler;
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
public class NettyClient implements ChannelFutureListener {
    private final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    public static final long RECONNECT_INTERVAL = 5;

    private AtomicLong nextSequence = new AtomicLong(0);
    private String key;
    private String host;
    private int port;
    private volatile ChannelFuture future;
    private EventLoopGroup group;
    private volatile boolean connected = false;

    public NettyClient(String key, String host, int port, EventLoopGroup group) {
        this.key = key;
        this.host = host;
        this.port = port;
        this.group = group;
    }

    public void connect() {
        synchronized (this) {
            if (connected) {
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
                        socketChannel.pipeline().addLast(new ClientHandler(NettyClient.this));
                }
            });
        future = bootstrap.connect(host, port).addListener(this);
    }

    private void ensureConnected() throws InterruptedException {
        if (!connected) {
            synchronized (this) {
                if (!connected) {
                    Integer timeout = PhobosConfig.getInstance().getClient().getResolveTimeout();
                    this.wait(timeout * 1000);
                    if (!connected) {
                        // FIXME
                        throw new RuntimeException("timeout");
                    }
                }
            }
        }
    }

    public PhobosResponse request(PhobosRequest request, long timeout, TimeUnit unit) throws InterruptedException {
        ensureConnected();

        long sequenceId = nextSequence.addAndGet(1);
        request.getHeader().setSequenceId(sequenceId);
        Promise<PhobosResponse> promise = ClientContext.getInstance().newPromise(sequenceId);
        try {
            future.channel().writeAndFlush(request).await(PhobosConfig.getInstance().getClient().getRequestTimeout() * 1000);
        } catch (InterruptedException e) {
            ClientContext.getInstance().cancelPromise(sequenceId);
            // FIXME
            throw new RuntimeException(e);
        }

        try {
            return promise.get(timeout, unit);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // FIXME
            throw new RuntimeException(e);
        } finally {
            ClientContext.getInstance().cancelPromise(sequenceId);
        }
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        synchronized (this) {
            connected = future.isSuccess();
            if (connected) {
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

    public String toString() {
        return key + "(" + host + ":" + port + ")";
    }
}
