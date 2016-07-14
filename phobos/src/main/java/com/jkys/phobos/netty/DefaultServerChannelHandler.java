package com.jkys.phobos.netty;

import com.jkys.phobos.netty.router.DefaultPhobosRouter;
import com.jkys.phobos.netty.router.PhobosRouter;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zdj on 2016/7/5.
 */
public class DefaultServerChannelHandler extends AbstractServerChannelHandler {

    private PhobosRouter router = new DefaultPhobosRouter();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(!(msg instanceof PhobosRequest))
            throw new ClassCastException("msg type must is PhobosRequest");

        PhobosRequest phobosRequest = (PhobosRequest)msg;

        PhobosResponse phobosResponse = router.route(phobosRequest);

        ctx.writeAndFlush(phobosResponse);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
