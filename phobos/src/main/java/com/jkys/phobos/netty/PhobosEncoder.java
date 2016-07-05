package com.jkys.phobos.netty;

import com.jkys.phobos.codec.ICodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by frio on 16/7/4.
 */
public class PhobosEncoder extends MessageToByteEncoder {
    private ICodec codec;

    public ICodec getCodec() {
        return codec;
    }

    public void setCodec(ICodec codec) {
        this.codec = codec;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {

    }
}
