package com.jkys.phobos.exception;

/**
 * Created by zdj on 16-12-14.
 */
public class PhobosException extends RuntimeException {
    public ErrorCode code;
    public String message;

    public PhobosException(ErrorCode code, String message){
        super();
        this.code = code;
        this.message = message;
    }

    public PhobosException(ErrorCode code, String message, Exception e){
        super(e);
        this.message = message;
        this.code = code;
    }
}
