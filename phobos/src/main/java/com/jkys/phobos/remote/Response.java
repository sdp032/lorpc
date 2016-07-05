package com.jkys.phobos.remote;

/**
 * Created by frio on 16/7/4.
 */
public class Response {

    private long sequenceId; /** we got from request **/

    private boolean isSuccess;

    private String errCode;

    private String errMessage;

    private String applicationErrorType;

    private Object data;

    public long getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(long sequenceId) {
        this.sequenceId = sequenceId;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
