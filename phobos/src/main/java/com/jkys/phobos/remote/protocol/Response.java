package com.jkys.phobos.remote.protocol;

import org.msgpack.annotation.Message;

/**
 * Created by frio on 16/7/4.
 */
@Message
public class Response {

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
}
