package com.jkys.phobos.netty.channel;

import com.jkys.phobos.client.InvokeInfo;
import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.netty.NettyClient;
import com.jkys.phobos.netty.listener.PhobosChannelActiveEvent;
import com.jkys.phobos.netty.listener.PhobosChannelReadEvent;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import com.jkys.phobos.remote.protocol.Request;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zdj on 2016/7/6.
 */
public class DefaultClientChannelHandler extends AbstractClientChannelHandler {

    private Logger logger = LoggerFactory.getLogger(DefaultClientChannelHandler.class);

    public DefaultClientChannelHandler(NettyClient source) {
        super(source);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        PhobosRequest request = new PhobosRequest(new Header(),new Request());
        request.getHeader().setSerializationType(PhobosClientContext.getInstance().getSerializationType());
        request.getRequest().setTraceId(new byte[16]);//TODO 规则待定
        request.getRequest().setServiceName("server-info");
        request.getRequest().setServiceVersion("V1");
        request.getRequest().setMethodName("server-info");
        request.getRequest().setClientAppName(PhobosClientContext.getInstance().getClientAppName());

        ctx.writeAndFlush(request);

        InvokeInfo invokeInfo = new InvokeInfo();
        invokeInfo.setRequest(request);
        PhobosClientContext.getInstance().setInvokeInfo(invokeInfo);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(!(msg instanceof PhobosResponse)){
            throw new ClassCastException("msg type must is PhobosResponse");
        }

        PhobosResponse phobosResponse = (PhobosResponse)msg;
        InvokeInfo invokeInfo = PhobosClientContext.getInstance().removeInvokeInfo(phobosResponse.getHeader().getSequenceId());
        logger.info("channelRead -> {}", invokeInfo.getRequest().getHeader().getSequenceId());
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
