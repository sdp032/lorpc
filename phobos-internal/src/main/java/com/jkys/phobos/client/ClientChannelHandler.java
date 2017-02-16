package com.jkys.phobos.client;

import com.jkys.phobos.util.Promise;
import com.jkys.phobos.protocol.PhobosResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by lo on 1/7/17.
 */
public class ClientChannelHandler extends SimpleChannelInboundHandler<PhobosResponse> {
    private Logger logger = LoggerFactory.getLogger(ClientChannelHandler.class);
    private ClientConnection client;

    ClientChannelHandler(ClientConnection client) {
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
        switch (response.getHeader().getType()) {
            case Default:
                Promise<PhobosResponse> promise = ClientContext.getInstance().getPromise(response.getHeader().getSequenceId());
                if (promise != null) {
                    promise.setSuccess(response);
                } else {
                    logger.warn("unhandled response: {}", response.getHeader().getSequenceId());
                }
                break;
            case Shutdown:
                client.markTemporaryShutdown();
                break;
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
