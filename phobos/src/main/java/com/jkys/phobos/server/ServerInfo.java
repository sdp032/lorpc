package com.jkys.phobos.server;

import com.jkys.phobos.config.PhobosConfig;
import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zdj on 2016/7/12.
 */
@Message
public class ServerInfo implements Serializable {

    private String serverAppName;
    private List<String> serviceList = new ArrayList();

    public ServerInfo() {
        this.serverAppName = PhobosConfig.getInstance().getAppName();
    }

    public List<String> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<String> serviceList) {
        this.serviceList = serviceList;
    }

    public String getServerAppName() {
        return serverAppName;
    }

    public void setServerAppName(String serverAppName) {
        this.serverAppName = serverAppName;
    }
}
