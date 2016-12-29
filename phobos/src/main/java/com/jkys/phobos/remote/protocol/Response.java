package com.jkys.phobos.remote.protocol;

import com.jkys.phobos.util.ByteUitl;
import com.jkys.phobos.util.CommonUtil;
import com.jkys.phobos.util.SerializaionUtil;
import io.netty.buffer.ByteBuf;
import org.msgpack.annotation.Message;

/**
 * Created by frio on 16/7/4.
 */
@Message
public class Response {

    private static final String CHARSET = "UTF-8";

    private boolean success;

    private String errCode;

    private String errMessage;

    private String applicationErrorType;

    private byte[] data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getApplicationErrorType() {
        return applicationErrorType;
    }

    public void setApplicationErrorType(String applicationErrorType) {
        this.applicationErrorType = applicationErrorType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    static public byte[] toBytes(Response response, byte type) throws Exception{

        byte[] success = response.isSuccess() ? new byte[]{1} : new byte[]{0};

        if(response.isSuccess()){
            return CommonUtil.concatBytes(success, response.getData());
        }else {
            byte[] errCode = response.getErrCode() == null ? new byte[0] : response.getErrCode().getBytes(Response.CHARSET);
            byte[] errCodeLen = new byte[]{(byte) errCode.length};
            byte[] errMessage = response.getErrMessage() == null ? new byte[0] : response.getErrMessage().getBytes(Response.CHARSET);
            short s = (short)errMessage.length;
            byte[] errMessageLen = ByteUitl.shortToBytes((short)errMessage.length);
            byte[] applicationErrorType = response.getApplicationErrorType() == null ? new byte[0] : response.getApplicationErrorType().getBytes(Response.CHARSET);
            byte[] applicationErrorTypeLen = new byte[]{(byte)applicationErrorType.length};
            return CommonUtil.concatBytes(success, errCodeLen, errCode, errMessageLen, errMessage, applicationErrorTypeLen, applicationErrorType, response.getData());
        }
    }

    static public Response toResponse(ByteBuf in, byte type, int size) throws Exception{

        Response response = new Response();

        boolean success = in.readByte() == (byte) 1;
        response.setSuccess(success);
        if(success){
            byte[] data = new byte[size - 1];
            in.readBytes(data);
            response.setData(data);
        }else {
            byte[] code = new byte[in.readByte()];
            if(code.length > 0){
                in.readBytes(code);
                response.setErrCode(new String(code, Response.CHARSET));
            }
            short errMessageLen = ByteUitl.bytesToShort(new byte[]{in.readByte(), in.readByte()});
            byte[] errMessage = new byte[errMessageLen];
            if(errMessage.length > 0){
                in.readBytes(errMessage);
                response.setErrMessage(new String(errMessage, Response.CHARSET));
            }
            byte[] applicationErrorType = new byte[in.readByte()];
            if(applicationErrorType.length > 0){
                in.readBytes(applicationErrorType);
                response.setApplicationErrorType(new String(applicationErrorType, Response.CHARSET));
            }
            byte[] body = new byte[size - 5 - code.length - errMessage.length - applicationErrorType.length];
            if(body.length > 0){
                in.readBytes(body);
                response.setData(body);
            }
        }

        return response;
    }
}
