package com.jkys.phobos.exception;

/**
 * Created by lo on 1/17/17.
 */
public class EnvException extends RuntimeException {
    public EnvException(String message, Throwable t) {
        super(message, t);
    }
}
