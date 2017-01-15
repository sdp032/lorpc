package com.jkys.phobos.config;

import com.jkys.phobos.serialization.SerializationType;

/**
 * Created by lo on 1/5/17.
 */
public class ClientConfig {
    public static final String NAME = "_phobosClientConfig";
    public static final int DEFAULT_RESOLVE_TIMEOUT = 10;
    public static final int DEFAULT_REQUEST_TIMEOUT = 60;

    private SerializationType serializationType;
    private Integer resolveTimeout;
    private Integer requestTimeout;

    public ClientConfig() {
        serializationType = SerializationType.Json;
        resolveTimeout = DEFAULT_RESOLVE_TIMEOUT;
        requestTimeout = DEFAULT_REQUEST_TIMEOUT;
    }

    public ClientConfig(ClientConfig other) {
        this.serializationType = other.serializationType;
        this.resolveTimeout = other.resolveTimeout;
        this.requestTimeout = other.requestTimeout;
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
