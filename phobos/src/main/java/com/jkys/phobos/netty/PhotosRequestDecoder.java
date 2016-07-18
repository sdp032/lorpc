package com.jkys.phobos.netty;

import com.jkys.phobos.codec.ICodec;
import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.Request;
import com.jkys.phobos.util.SerializaionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by zdj on 2016/7/14.
 *
 * 解决TCP粘包拆包及反序列化
 */
    public class PhotosRequestDecoder extends ByteToMessageDecoder {

    ICodec codec;

    private final static int HEAD_SIZE = 26;

    public ICodec getCodec() {
        return codec;
    }

    public void setCodec(ICodec codec) {
        this.codec = codec;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if(in.readableBytes() < HEAD_SIZE){ //缓冲区没有完整的header信息 返回 继续接受TCP数据
            return;
        }

        in.markReaderIndex();   //标记流的读取位置

        //读取header信息
        short protocolVersion = in.readShort();     //2
        char serializationType = in.readChar();     //2
        char type = in.readChar();                  //2
        int size = in.readInt();                    //4
        long sequenceId = in.readLong();            //8
        long timestamp = in.readLong();             //8

        if(in.readableBytes() < size){  //缓冲区内剩余的字节流小于request的字节流
            in.resetReaderIndex();  //将指针重新指向读取header时的下标
            return;
        }

        Header header = new Header();
        header.setProtocolVersion(protocolVersion);
        header.setSerializationType(serializationType);
        header.setType(type);
        header.setSize(size);
        header.setSequenceId(sequenceId);
        header.setTimestamp(timestamp);

        byte[] body = new byte[size];
        in.readBytes(body);

        Request request = SerializaionUtil.bytesToObject(body,Request.class,header.getSerializationType());

        out.add(new PhobosRequest(header,request));
    }
}
