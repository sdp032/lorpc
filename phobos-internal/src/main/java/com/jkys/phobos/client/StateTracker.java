package com.jkys.phobos.client;

/**
 * Created by lo on 2/8/17.
 */
public interface StateTracker {
    void changeState(ConnectionState state);

    ConnectionState getState();

    boolean isConnected();
}
