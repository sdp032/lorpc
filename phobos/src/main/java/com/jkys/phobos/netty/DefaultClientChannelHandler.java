package com.jkys.phobos.netty;

import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.netty.listener.PhobosChannelActiveEvent;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import com.jkys.phobos.remote.protocol.Request;
import com.jkys.phobos.server.PhobosContext;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zdj on 2016/7/6.
 */
public class DefaultClientChannelHandler extends AbstractClientChannelHandler {

    public DefaultClientChannelHandler(NettyClient source) {
        super(source);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        PhobosRequest request = new PhobosRequest(new Header(),new Request());
        request.getHeader().setSerializationType(PhobosClientContext.getInstance().getSerializationType());
        request.getRequest().setTraceId(1l);//TODO 规则待定
        request.getRequest().setServiceName("server-info");
        request.getRequest().setServiceVersion("V1");
        request.getRequest().setMethodName("server-info");
        request.getRequest().setClientAppName(PhobosClientContext.getInstance().getClientAppName());

        ctx.writeAndFlush(request);

        notify(new PhobosChannelActiveEvent(this));
        //TODO 唤醒主线程  需要响应的地方
        synchronized (getSource()){
            getSource().setConnect(true);
            getSource().notify();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(!(msg instanceof PhobosResponse)){
            throw new ClassCastException("msg type must is PhobosResponse");
        }

        PhobosResponse phobosResponse = (PhobosResponse)msg;

        System.out.println("客户端收到");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
