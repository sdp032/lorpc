package com.jkys.phobos.client;

import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;
import com.jkys.phobos.util.SerializaionUtil;

/**
 * Created by zdj on 2016/7/15.
 */
public class InvokeInfo {

    private boolean isTimeOut = true;

    private PhobosRequest request;

    private PhobosResponse response;

    public PhobosRequest getRequest() {
        return request;
    }

    public PhobosResponse getResponse() {
        return response;
    }

    public void setRequest(PhobosRequest request) {
        this.request = request;
    }

    public void setResponse(PhobosResponse response) {
        this.response = response;
    }

    public boolean isTimeOut() {
        return isTimeOut;
    }

    public void setTimeOut(boolean timeOut) {
        isTimeOut = timeOut;
    }

}
