package com.jkys.phobos.client;

import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.PhobosResponse;

/**
 * Created by zdj on 2016/7/15.
 */
public class InvokeInfo {

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
}
