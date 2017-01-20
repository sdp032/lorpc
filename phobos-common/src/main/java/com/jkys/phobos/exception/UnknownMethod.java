package com.jkys.phobos.exception;

/**
 * Created by lo on 1/17/17.
 */
public class UnknownMethod extends PhobosException {
    private String name;

    public UnknownMethod(String name) {
        super(ErrorCode.UNKNOWN_METHOD, "not found: " + name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
