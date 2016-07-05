package com.jkys.phobos.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by frio on 16/7/4.
 */
public class PhobosCodec implements ICodec {
    static Logger LOG = LoggerFactory.getLogger(PhobosCodec.class);

    public void encode(ChannelPromise channelPromise, ByteBuf out, Object message) {

    }

    public void decode(ChannelPromise channelPromise, ByteBuf in) {

    }
}
