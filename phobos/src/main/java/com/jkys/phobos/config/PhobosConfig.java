package com.jkys.phobos.config;

public class PhobosConfig {
    private RegistryConfig registry = new RegistryConfig();
    private ServerConfig server = new ServerConfig();
    private ClientConfig client = new ClientConfig();

    private static PhobosConfig phobosContext = null;

    private PhobosConfig() {
    }

    public synchronized static PhobosConfig getInstance() {
        if (phobosContext == null) {
            phobosContext = new PhobosConfig();
        }
        return phobosContext;
    }

    public String getAppName() {
        return registry.getAppName();
    }

    public RegistryConfig getRegistry() {
        return registry;
    }

    public void setRegistry(RegistryConfig registry) {
        this.registry = registry;
    }

    public ServerConfig getServer() {
        return server;
    }

    public void setServer(ServerConfig server) {
        this.server = server;
    }

    public ClientConfig getClient() {
        return client;
    }

    public void setClient(ClientConfig client) {
        this.client = client;
    }
}
