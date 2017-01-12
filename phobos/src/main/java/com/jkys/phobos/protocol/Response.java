package com.jkys.phobos.protocol;

import com.jkys.phobos.util.ByteUitl;
import com.jkys.phobos.util.CommonUtil;
import org.msgpack.annotation.Message;

import java.util.Arrays;

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

    static public Response toResponse(byte[] bytes) throws Exception{

        Response response = new Response();
        int index = 0;

        boolean success = bytes[index++] == (byte) 1;
        response.setSuccess(success);
        if(success){
            response.setData(Arrays.copyOfRange(bytes, 1, bytes.length));
        }else {
            byte[] code = Arrays.copyOfRange(bytes, index + 1, bytes[index++] + index);
            if(code.length > 0){
                response.setErrCode(new String(code, Response.CHARSET));
            }
            index = index + code.length;
            short errMessageLen = ByteUitl.bytesToShort(Arrays.copyOfRange(bytes, index, index + 2));
            index = index + 2;
            byte[] errMessage = Arrays.copyOfRange(bytes, index, index + errMessageLen);
            if(errMessage.length > 0){
                response.setErrMessage(new String(errMessage, Response.CHARSET));
            }
            index = index + errMessageLen;
            byte[] applicationErrorType = Arrays.copyOfRange(bytes, index + 1, bytes[index++] + index);
            if(applicationErrorType.length > 0){
                response.setApplicationErrorType(new String(applicationErrorType, Response.CHARSET));
            }
            index = index + applicationErrorType.length;
            byte[] body = Arrays.copyOfRange(bytes, index, bytes.length);
            if(body.length > 0){
                response.setData(body);
            }
        }

        return response;
    }
}
