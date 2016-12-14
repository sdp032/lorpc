package com.jkys.phobos.exception;

/**
 * Created by zdj on 16-12-14.
 */
public class PhobosException extends RuntimeException {

    public String code;
    public String message;

    public PhobosException(){}

    public PhobosException(String code, String message){
        super();
        this.code = code;
        this.message = message;
    }

    public PhobosException(String code, String message, Exception e){
        super(e);
        this.message = message;
        this.code = code;
    }
}
