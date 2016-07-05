package com.jkys.phobos.netty;

import com.jkys.phobos.codec.ICodec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by frio on 16/7/4.
 */
public class PhotosDecoder extends ByteToMessageDecoder {
    ICodec codec;

    public ICodec getCodec() {
        return codec;
    }

    public void setCodec(ICodec codec) {
        this.codec = codec;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

    }
}
