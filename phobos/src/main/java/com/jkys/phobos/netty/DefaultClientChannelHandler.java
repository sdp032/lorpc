package com.jkys.phobos.netty;

import com.jkys.phobos.client.InvokeInfo;
import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.netty.listener.PhobosChannelActiveEvent;
import com.jkys.phobos.netty.listener.PhobosChannelReadEvent;
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

        InvokeInfo invokeInfo = new InvokeInfo();
        invokeInfo.setRequest(request);
        PhobosClientContext.getInstance().getInvokeInfoMap().put(request.getHeader().getSequenceId(),invokeInfo);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(!(msg instanceof PhobosResponse)){
            throw new ClassCastException("msg type must is PhobosResponse");
        }

        PhobosResponse phobosResponse = (PhobosResponse)msg;
        InvokeInfo invokeInfo = PhobosClientContext.getInstance().getInvokeInfoMap().remove(phobosResponse.getHeader().getSequenceId());
        if(invokeInfo == null){
            throw new NullPointerException("invokeInfo is null for sequenceId : " + phobosResponse.getHeader().getSequenceId());
        }
        invokeInfo.setResponse(phobosResponse);
        if("server-info".equals(invokeInfo.getRequest().getRequest().getServiceName())){
            notify(new PhobosChannelActiveEvent(getSource(),invokeInfo));
        }else{
            notify(new PhobosChannelReadEvent(this,invokeInfo));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
