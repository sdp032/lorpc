package com.jkys.phobos.netty;

import com.jkys.phobos.codec.ICodec;
import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.remote.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zdj on 2016/7/13.
 */
public class PhobosRequestEncoder extends MessageToByteEncoder <PhobosRequest>{
    private ICodec codec;

    public ICodec getCodec() {
        return codec;
    }

    public void setCodec(ICodec codec) {
        this.codec = codec;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, PhobosRequest msg, ByteBuf out) throws Exception {

        if(msg == null)
            throw new NullPointerException("PhobosRequest is null");

        Header header = msg.getHeader();
        Request request = msg.getRequest();

        if(header == null)
            throw new NullPointerException("Header is null");
        if(request == null)
            throw new NullPointerException("Request is null");

        byte[] body = null;

        if(header.getSerializationType() == Header.SerializationType.MAGPACK.serializationType){
            body = MsgpackUtil.MESSAGE_PACK.write(request);
        }
        //TODO 其他序列化方式

        header.setSize(body.length);

        //header 长度固定26
        out.writeShort(header.getProtocolVersion());    //2
        out.writeChar(header.getSerializationType());   //2
        out.writeChar(header.getType());                //2
        out.writeInt(header.getSize());                 //4
        out.writeLong(header.getSequenceId());          //8
        out.writeLong(header.getTimestamp());           //8

        //request 长度由header为size
        out.writeBytes(body);
    }

}
