package com.jkys.phobos.client;

import com.jkys.phobos.util.Promise;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by lo on 1/5/17.
 */
public class ClientContext {
    private static ClientContext clientContext = new ClientContext();
    private ConcurrentHashMap<Long, Promise<PhobosResponse>> promises = new ConcurrentHashMap<>();
    private ClientBus clientBus = new ClientBus();

    public static ClientContext getInstance() {
        return clientContext;
    }

    public PhobosResponse request(PhobosRequest request, long timeout, TimeUnit unit) throws InterruptedException {
        return clientBus.request(request, timeout, unit);
    }

    public Promise<PhobosResponse> newPromise(long sequenceId) {
        Promise<PhobosResponse> promise = new Promise<>();
        if (promises.putIfAbsent(sequenceId, promise) != null) {
            throw new RuntimeException("duplicated sequenceId: " + sequenceId);
        }
        return promise;
    }

    public Promise<PhobosResponse> getPromise(long sequenceId) {
        return promises.get(sequenceId);
    }

    public void cancelPromise(long sequenceId) {
        promises.remove(sequenceId);
    }
}
