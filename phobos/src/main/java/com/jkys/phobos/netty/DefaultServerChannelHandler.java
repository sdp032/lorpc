package com.jkys.phobos.netty;

import com.jkys.phobos.remote.protocol.Response;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zdj on 2016/7/5.
 */
public class DefaultServerChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.getClass().getName());

        String s = "收到";
        ByteBuf bb = Unpooled.buffer(s.length());
        bb.writeBytes(s.getBytes());
        ctx.writeAndFlush(bb);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
