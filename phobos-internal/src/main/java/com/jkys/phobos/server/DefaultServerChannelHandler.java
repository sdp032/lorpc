package com.jkys.phobos.server;

import com.jkys.phobos.exception.ErrorCode;
import com.jkys.phobos.protocol.Response;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;
import com.jkys.phobos.serialization.SerializationType;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zdj on 2016/7/5.
 */
public class DefaultServerChannelHandler extends ChannelHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(DefaultServerChannelHandler.class);
    private ServerContext context = ServerContext.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!(msg instanceof PhobosRequest))
            throw new ClassCastException("msg type must is PhobosRequest");
        PhobosRequest phobosRequest = (PhobosRequest)msg;
        PhobosResponse phobosResponse = route(phobosRequest);
        ctx.writeAndFlush(phobosResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private PhobosResponse route(PhobosRequest request) throws Exception {
        String serviceName = request.getRequest().getServiceName();
        String serviceVersion = request.getRequest().getServiceVersion();
        Provider provider = context.getProvider(serviceName, serviceVersion);
        if (provider == null) {
            PhobosResponse phobosResponse = new PhobosResponse(request.getHeader(), new Response());
            phobosResponse.getResponse().setSuccess(false);
            phobosResponse.getResponse().setErrCode(ErrorCode.UNKNOWN_SERVICE.name());
            return phobosResponse;
        }
        SerializationType serializationType = SerializationType.get(request.getHeader().getSerializationType());
        return new PhobosResponse(request.getHeader(), provider.invoke(serializationType, request.getRequest()));
    }
}
