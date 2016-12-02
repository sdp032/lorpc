package com.jkys.phobos.client;

import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;

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

    public void setRequest(PhobosRequest request) {
        this.request = request;
    }

    public PhobosResponse getResponse() {
        return response;
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
