package com.jkys.phobos.netty.handler;

import com.jkys.phobos.client.ClientContext;
import com.jkys.phobos.netty.NettyClient;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import com.jkys.phobos.util.Promise;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by lo on 1/7/17.
 */
public class ClientHandler extends SimpleChannelInboundHandler<PhobosResponse> {
    private Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private NettyClient client;

    public ClientHandler(NettyClient client) {
        this.client = client;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // FIXME exception
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, PhobosResponse response) throws Exception {
        Promise<PhobosResponse> promise = ClientContext.getInstance().getPromise(response.getHeader().getSequenceId());
        if (promise != null) {
            promise.setSuccess(response);
        } else {
            logger.warn("unhandled response: {}", response.getHeader().getSequenceId());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.warn("channel closed: {}", client);
        ctx.channel().eventLoop().schedule(() -> {
            logger.info("reconnecting to {}", client);
            client.connect();
        }, NettyClient.RECONNECT_INTERVAL, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }
}
