package com.jkys.phobos.codec;

import com.jkys.phobos.protocol.BodyType;
import com.jkys.phobos.protocol.Header;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.Request;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zdj on 2016/7/14.
 *
 * 解决TCP粘包拆包及反序列化
 */
public class PhobosRequestDecoder extends ByteToMessageDecoder {
    private static Logger logger = LoggerFactory.getLogger(PhobosResponseDecoder.class);

    private final static int HEAD_SIZE = 24;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if(in.readableBytes() < HEAD_SIZE){ //缓冲区没有完整的header信息 返回 继续接受TCP数据
            return;
        }

        in.markReaderIndex();   //标记流的读取位置

        //读取header信息
        short protocolVersion = in.readShort();     //2
        byte serializationType = in.readByte();     //1
        byte type = in.readByte();                  //1
        int size = in.readInt();                    //4
        long sequenceId = in.readLong();            //8
        long timestamp = in.readLong();             //8


        if(in.readableBytes() < size){  //缓冲区内剩余的字节流小于request的字节流
            in.resetReaderIndex();  //将指针重新指向读取header时的下标
            return;
        }

        byte[] bytes = new byte[size];
        in.readBytes(bytes);

        Header header = new Header();
        header.setProtocolVersion(protocolVersion);
        header.setSerializationType(serializationType);
        BodyType bodyType = BodyType.getBodyType(type);
        if (bodyType == null) {
            logger.debug("invalid type: " + type);
            ctx.close();
            return;
        }
        header.setType(bodyType);
        header.setSize(size);
        header.setSequenceId(sequenceId);
        header.setTimestamp(timestamp);

        Request request = null;
        switch (bodyType) {
            case Default:
                request = Request.toRequest(bytes);
                break;
            default:
                logger.warn("unsupported type: " + bodyType);
                return;
        }
        out.add(new PhobosRequest(header, request));
    }
}
