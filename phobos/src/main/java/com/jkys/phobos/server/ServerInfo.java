package com.jkys.phobos.server;

import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zdj on 2016/7/12.
 */
@Message
public class ServerInfo implements Serializable{

    public ServerInfo(){
        this.serverName = PhobosContext.getInstance().getServerName();
    }

    private String serverName;

    private List<String> serviceList = new ArrayList();

    public List<String> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<String> serviceList) {
        this.serviceList = serviceList;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
