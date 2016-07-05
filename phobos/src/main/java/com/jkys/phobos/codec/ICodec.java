package com.jkys.phobos.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPromise;

import java.io.IOException;

/**
 * Created by frio on 16/7/4.
 */
public interface ICodec {
    /**
     * do encode operation
     * @param channelPromise used to get local address, remote address.etc.
     * @param out write encoded byte[] to buffer
     * @param message message that need to be encoded
     */
    void encode(ChannelPromise channelPromise, ByteBuf out, Object message) throws IOException;

    /**
     * do decode operation
     * @param channelPromise used to get local address, remote address.etc.
     * @param in decode from byte[]
     */
    void decode(ChannelPromise channelPromise, ByteBuf in) throws IOException;
}
