package com.jkys.phobos.codec;

import com.jkys.phobos.protocol.Header;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by zdj on 2016/7/13.
 */
public class PhobosRequestEncoder extends MessageToByteEncoder <PhobosRequest>{

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

        byte[] body = request.toBytes();
        if (body != null) {
            header.setSize(body.length);
        } else {
            header.setSize(0);
        }

        //header 长度固定24
        out.writeShort(header.getProtocolVersion());    //2
        out.writeByte(header.getSerializationType());   //1
        if (header.getType() == null) {
            throw new RuntimeException("got null type");
        }
        out.writeByte(header.getType().getType());      //1
        out.writeInt(header.getSize());                 //4
        out.writeLong(header.getSequenceId());          //8
        out.writeLong(header.getTimestamp());           //8

        if (body != null) {
            out.writeBytes(body);
        }
    }

}
