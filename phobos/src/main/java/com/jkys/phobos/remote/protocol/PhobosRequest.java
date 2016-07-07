package com.jkys.phobos.remote.protocol;

/**
 * Created by zdj on 2016/7/7.
 */
public class PhobosRequest {

    private Header header;

    private Request request;

    public PhobosRequest(){

    }

    public PhobosRequest(Header header,Request request){
        this.header = header;
        this.request = request;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
