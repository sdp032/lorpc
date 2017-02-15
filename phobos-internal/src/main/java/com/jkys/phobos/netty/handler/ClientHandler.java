package com.jkys.phobos.netty.handler;

import com.jkys.phobos.netty.ClientConnection;
import com.jkys.phobos.util.Promise;
import com.jkys.phobos.client.ClientContext;
import com.jkys.phobos.protocol.PhobosResponse;
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
    private ClientConnection client;

    public ClientHandler(ClientConnection client) {
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
        client.markDisconnected();
        ctx.channel().eventLoop().schedule(() -> {
            logger.info("reconnecting to {}", client);
            client.connect();
        }, ClientConnection.RECONNECT_INTERVAL, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }
}
