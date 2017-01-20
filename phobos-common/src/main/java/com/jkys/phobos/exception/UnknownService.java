package com.jkys.phobos.exception;

/**
 * Created by lo on 1/17/17.
 */
public class UnknownService extends PhobosException {
    private String name;
    private String version;

    public UnknownService(String name, String version) {
        super(ErrorCode.UNKNOWN_SERVICE, "not found: " + name + ":" + version);
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
