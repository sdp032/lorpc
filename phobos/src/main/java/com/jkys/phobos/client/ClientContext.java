package com.jkys.phobos.client;

/**
 * Created by lo on 1/5/17.
 */
public class ClientContext {
    private static ClientContext clientContext;

    public synchronized static ClientContext getInstance() {
        if (clientContext == null) {
            clientContext = new ClientContext();
        }
        return clientContext;
    }
}
