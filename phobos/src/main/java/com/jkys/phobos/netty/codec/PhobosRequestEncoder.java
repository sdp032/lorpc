package com.jkys.phobos.netty.codec;

import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.remote.protocol.*;
import com.jkys.phobos.util.SerializaionUtil;
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

        //byte[] body = SerializaionUtil.objectToBytes(request,header.getSerializationType());
        byte[] body = null;
        if(header.getType() == (byte) 0){
            body = Request.toBytes(request, header.getSerializationType());
        }

        header.setSize(body.length);

        //header 长度固定24
        out.writeShort(header.getProtocolVersion());    //2
        out.writeByte(header.getSerializationType());   //1
        out.writeByte(header.getType());                //1
        out.writeInt(header.getSize());                 //4
        out.writeLong(header.getSequenceId());          //8
        out.writeLong(header.getTimestamp());           //8

        //request
        out.writeBytes(body);
    }

}
