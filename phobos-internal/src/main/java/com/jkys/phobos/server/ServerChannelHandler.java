package com.jkys.phobos.server;

import com.jkys.phobos.exception.ErrorCode;
import com.jkys.phobos.protocol.*;
import com.jkys.phobos.serialization.SerializationType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zdj on 2016/7/5.
 */
@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {
    private static final int DEFAULT_THREADS;
    private static Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class);
    private ServerContext context = ServerContext.getInstance();
    private ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private ExecutorService executor = Executors.newFixedThreadPool(DEFAULT_THREADS);
    private volatile boolean isShuttingDown = false;

    static {
        DEFAULT_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors() * 8);
    }

    void setThreads(Integer threads) {
        if (threads != null) {
            executor = Executors.newFixedThreadPool(threads);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof PhobosRequest))
            throw new ClassCastException("msg type must is PhobosRequest");
        PhobosRequest phobosRequest = (PhobosRequest)msg;
        request(ctx, phobosRequest);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        synchronized (this) {
            if (isShuttingDown) {
                this.notifyAll();
            }
        }
    }

    void stop() {
        isShuttingDown = true;
        Header header = new Header();
        header.setType(BodyType.Shutdown);
        PhobosResponse response = new PhobosResponse(header, null);
        channels.writeAndFlush(response);
    }

    void waitStop(long timeout, TimeUnit unit) throws InterruptedException {
        synchronized (this) {
            while (channels.size() > 0) {
                if (timeout <= 0) {
                    throw new RuntimeException("shutdown timeout");
                }
                long start = System.currentTimeMillis();
                this.wait(TimeUnit.MILLISECONDS.convert(timeout, unit));
                timeout -= unit.convert(System.currentTimeMillis() - start,
                        TimeUnit.MILLISECONDS);
            }
        }
    }

    private void request(ChannelHandlerContext ctx, PhobosRequest request) throws Exception {
        String serviceName = request.getRequest().getServiceName();
        String serviceVersion = request.getRequest().getServiceVersion();
        Provider provider = context.getProvider(serviceName, serviceVersion);
        if (provider == null) {
            PhobosResponse phobosResponse = new PhobosResponse(request.getHeader(), new Response());
            phobosResponse.getResponse().setSuccess(false);
            phobosResponse.getResponse().setErrCode(ErrorCode.UNKNOWN_SERVICE.name());
            ctx.writeAndFlush(phobosResponse);
            return;
        }
        SerializationType serializationType = SerializationType.get(request.getHeader().getSerializationType());
        executor.execute(() -> {
            PhobosResponse response = new PhobosResponse(request.getHeader(), provider.invoke(serializationType, request.getRequest()));
            ctx.writeAndFlush(response);
        });
    }
}
