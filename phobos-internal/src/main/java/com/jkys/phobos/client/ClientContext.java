package com.jkys.phobos.client;

import com.jkys.phobos.util.Promise;
import com.jkys.phobos.protocol.PhobosRequest;
import com.jkys.phobos.protocol.PhobosResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lo on 1/5/17.
 */
public class ClientContext {
    private static ClientContext clientContext = new ClientContext();
    private ConcurrentHashMap<Long, Promise<PhobosResponse>> promises = new ConcurrentHashMap<>();
    private ClientBus clientBus = new ClientBus();
    private AtomicLong nextSequence = new AtomicLong(0);

    public static ClientContext getInstance() {
        return clientContext;
    }

    public PhobosResponse request(PhobosRequest request, long timeout, TimeUnit unit) throws InterruptedException {
        return clientBus.request(request, timeout, unit);
    }

    public RequestPromise<PhobosResponse> newPromise() {
        long sequenceId = nextSequence.addAndGet(1);
        RequestPromise<PhobosResponse> promise = new RequestPromise<>(sequenceId);
        if (promises.putIfAbsent(sequenceId, promise) != null) {
            throw new RuntimeException("duplicated sequenceId: " + sequenceId);
        }
        return promise;
    }

    public Promise<PhobosResponse> getPromise(long sequenceId) {
        return promises.get(sequenceId);
    }

    public class RequestPromise<T> extends Promise<T> {
        private long sequenceId;

        RequestPromise(long sequenceId) {
            this.sequenceId = sequenceId;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return ClientContext.this.promises.remove(sequenceId) != null;
        }

        public long getSequenceId() {
            return sequenceId;
        }
    }
}
