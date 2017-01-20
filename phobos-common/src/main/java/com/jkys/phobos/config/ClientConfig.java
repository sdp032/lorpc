package com.jkys.phobos.config;

import com.jkys.phobos.annotation.ServiceUtil;
import com.jkys.phobos.serialization.SerializationType;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lo on 1/5/17.
 */
public class ClientConfig {
    public static final String NAME = "_phobosClientConfig";
    private static final int DEFAULT_RESOLVE_TIMEOUT = 10;
    private static final int DEFAULT_REQUEST_TIMEOUT = 60;
    private static final ClientConfig instance = new ClientConfig();

    private SerializationType serializationType;
    private Integer resolveTimeout;
    private Integer requestTimeout;
    private ConcurrentHashMap<String, String> presetAddresses = new ConcurrentHashMap<>();

    private ClientConfig() {
        serializationType = SerializationType.Json;
        resolveTimeout = DEFAULT_RESOLVE_TIMEOUT;
        requestTimeout = DEFAULT_REQUEST_TIMEOUT;
    }

    public static ClientConfig getInstance() {
        return instance;
    }

    public void presetAddress(String name, String version, String address) {
        if (!address.contains(":")) {
            address = address + ":" + ServerConfig.DEFAULT_PORT;
        }
        presetAddresses.put(ServiceUtil.serviceKey(name, version), address);
    }

    public String getPresetAddress(String name, String version) {
        return presetAddresses.get(ServiceUtil.serviceKey(name, version));
    }

    public SerializationType getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(SerializationType serializationType) {
        this.serializationType = serializationType;
    }

    public Integer getResolveTimeout() {
        return resolveTimeout;
    }

    public void setResolveTimeout(Integer resolveTimeout) {
        this.resolveTimeout = resolveTimeout;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}
