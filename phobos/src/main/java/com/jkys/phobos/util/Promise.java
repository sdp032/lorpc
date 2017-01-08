package com.jkys.phobos.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by lo on 1/6/17.
 */
public class Promise<T> implements Future<T> {
    private boolean done = false;
    private ExecutionException exception;
    private T result;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public boolean isSuccess() {
        synchronized (this) {
            if (!done) {
                throw new RuntimeException("not done");
            }
            return exception == null;
        }
    }

    public void sync() throws InterruptedException {
        synchronized (this) {
            if (!done) {
                this.wait();
            }
        }
    }

    public void sync(long timeout) throws InterruptedException, TimeoutException {
        synchronized (this) {
            if (!done) {
                this.wait(timeout);
                if (!done) {
                    throw new TimeoutException("wait timeout");
                }
            }
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        sync();
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        sync(unit.toMillis(timeout));
        if (exception != null) {
            throw exception;
        }
        return result;
    }

    public void setSuccess(T result) {
        synchronized (this) {
            if (done) {
                throw new RuntimeException("future has already setted");
            }
            this.result = result;
            this.done = true;
            this.notifyAll();
        }
    }

    public void setFailure(Throwable t) {
        synchronized (this) {
            if (done) {
                throw new RuntimeException("future has already setted");
            }
            this.exception = new ExecutionException(t);
            this.done = true;
            this.notifyAll();
        }
    }
}
