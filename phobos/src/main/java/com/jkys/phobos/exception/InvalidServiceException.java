package com.jkys.phobos.exception;

/**
 * Created by lo on 1/9/17.
 */
public class InvalidServiceException extends RuntimeException {
    private String service;

    public InvalidServiceException(String service) {
        this.service = service;
    }

    public String toString() {
        return "invalid service: " + service;
    }
}
