package com.jkys.phobos.config;

import com.jkys.phobos.exception.EnvException;
import com.jkys.phobos.util.CommonUtil;

/**
 * Created by lo on 1/5/17.
 */
public class ServerConfig {
    public static final String NAME = "_phobosServerConfig";
    public static final Integer DEFAULT_PORT = 3000;
    private static final String PORT_ENV_NAME = "APP_SERVICE_PORT";
    private static final String PORT_PROPERTY_NAME = "app.service_port";
    private static final String HOST_ENV_NAME = "APP_SERVICE_HOST";
    private static final String HOST_PROPERTY_NAME = "app.service_host";
    private static final ServerConfig instance = new ServerConfig();

    private String bindHost;
    private Integer bindPort;
    private Integer threads;
    private boolean blocking = false;

    private ServerConfig() {
        bindHost = System.getenv(HOST_ENV_NAME);
        if (bindHost == null) {
            bindHost = System.getProperty(HOST_PROPERTY_NAME);
        }
        if (bindHost == null) {
            try {
                bindHost = CommonUtil.getIpAddresses();
            } catch (Exception e) {
                throw new EnvException("get ip address fail", e);
            }
        }
        bindPort = DEFAULT_PORT;
        String envPort = System.getenv().get(PORT_ENV_NAME);
        if (envPort == null) {
            envPort = System.getProperty(PORT_PROPERTY_NAME);
        }
        if (envPort != null && !envPort.equals("")) {
            bindPort = Integer.valueOf(envPort);
        }
    }

    public static ServerConfig getInstance() {
        return instance;
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

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }
}
