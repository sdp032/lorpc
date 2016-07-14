package com.jkys.phobos.netty;

import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import com.jkys.phobos.remote.protocol.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by zdj on 2016/7/14.
 */
public class PhobosResponseEncoder extends MessageToByteEncoder<PhobosResponse> {

    protected void encode(ChannelHandlerContext cxt, PhobosResponse msg, ByteBuf out) throws Exception {

        if(msg == null){
            throw new NullPointerException("PhobosResponse is null");
        }

        Header header = msg.getHeader();
        Response response = msg.getResponse();

        if(header == null){
            throw new NullPointerException("Header is null");
        }
        if(response == null){
            throw new NullPointerException("Response is null");
        }

        byte[] body = null;

        if(header.getSerializationType() == Header.SerializationType.MAGPACK.serializationType){
            body = MsgpackUtil.MESSAGE_PACK.write(response);
        }

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
