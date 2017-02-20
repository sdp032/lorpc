package com.jkys.phobos.server;

import com.jkys.phobos.exception.ErrorCode;
import com.jkys.phobos.protocol.*;
import com.jkys.phobos.serialization.SerializationType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zdj on 2016/7/5.
 */
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(ServerChannelHandler.class);
    private ServerContext context = ServerContext.getInstance();
    private ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private boolean isShuttingDown = false;

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
        channels.remove(ctx.channel());
        super.channelInactive(ctx);
        synchronized (this) {
            if (isShuttingDown) {
                this.notifyAll();
            }
        }
    }

    void shutdown() {
        isShuttingDown = true;
        Header header = new Header();
        header.setType(BodyType.Shutdown);
        PhobosResponse response = new PhobosResponse(header, null);
        channels.writeAndFlush(response);
    }

    void waitShutdown() throws InterruptedException {
        synchronized (this) {
            while (channels.size() > 0) {
                // TODO timeout
                this.wait();
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
        // TODO execute thread pool
        PhobosResponse response = new PhobosResponse(request.getHeader(), provider.invoke(serializationType, request.getRequest()));
        ctx.writeAndFlush(response);
    }
}
