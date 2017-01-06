package com.jkys.phobos.config;

import com.jkys.phobos.util.CommonUtil;

/**
 * Created by lo on 1/5/17.
 */
public class ServerConfig {
    public static final String NAME = "_phobosServerConfig";
    public static final Integer DEFAULT_PORT = 3000;

    private String bindHost;
    private Integer bindPort;
    private boolean blocking = false;

    public ServerConfig() {
        try {
            bindHost = CommonUtil.getIpAddresses();
        } catch (Exception e) {
            // FIXME
            throw new RuntimeException(e);
        }
        bindPort = DEFAULT_PORT;
    }

    public ServerConfig(ServerConfig other) {
        this.bindHost = other.bindHost;
        this.bindPort = other.bindPort;
        this.blocking = other.blocking;
    }

    public String getAddress() {
        return bindHost + ":" + bindPort;
    }

    public String getBindHost() {
        return bindHost;
    }

    public void setBindHost(String bindHost) {
        this.bindHost = bindHost;
    }

    public Integer getBindPort() {
        return bindPort;
    }

    public void setBindPort(Integer bindPort) {
        this.bindPort = bindPort;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }
}
