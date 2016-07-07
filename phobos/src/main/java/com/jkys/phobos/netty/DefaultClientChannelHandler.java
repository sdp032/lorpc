package com.jkys.phobos.netty;

import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.Request;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

/**
 * Created by zdj on 2016/7/6.
 */
public class DefaultClientChannelHandler extends ChannelHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //建立链接时触发 获取服务端信息
        PhobosClientContext context = PhobosClientContext.getInstance();
        PhobosRequest request = new PhobosRequest(new Header(),new Request());
        request.getHeader().setSerializationType(context.getSerializationType());
        request.getHeader().setTimestamp(new Date().getTime());
        request.getHeader().setType(Header.Type.DEFAULT.type);
        request.getRequest().setClientAppName(context.getClientAppName());
        request.getRequest().setServiceName("serverInfo");

        String s = "hello";
        ByteBuf bb = Unpooled.buffer(s.length());
        bb.writeBytes(s.getBytes());
        ctx.writeAndFlush(bb);
        System.out.println("******请求服务器信息******");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //TODO 处理相应
        System.out.println(msg.getClass().getName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
