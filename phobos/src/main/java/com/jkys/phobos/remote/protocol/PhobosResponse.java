package com.jkys.phobos.remote.protocol;

/**
 * Created by zdj on 2016/7/7.
 */
public class PhobosResponse {

    private Header header;

    private Response response;

    public PhobosResponse(){

    }

    public PhobosResponse(Header header, Response response){
        this.header = header;
        this.response = response;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
