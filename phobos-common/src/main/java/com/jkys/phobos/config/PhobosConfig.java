package com.jkys.phobos.config;

public class PhobosConfig {
    private RegistryConfig registry = RegistryConfig.getInstance();
    private ServerConfig server = ServerConfig.getInstance();
    private ClientConfig client = ClientConfig.getInstance();

    private static final PhobosConfig instance = new PhobosConfig();

    private PhobosConfig() {
    }

    public static PhobosConfig getInstance() {
        return instance;
    }

    public RegistryConfig getRegistry() {
        return registry;
    }

    public ServerConfig getServer() {
        return server;
    }

    public ClientConfig getClient() {
        return client;
    }
}
