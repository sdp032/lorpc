package com.jkys.phobos.server;

/**
 * Created by zdj on 2016/11/17.
 */
public class ServiceBean {

    private String serviceName;
    private String group;
    private String version;
    private Object service;
    private Class interfaceClass;

    public ServiceBean() {
    }

    public ServiceBean(String serviceName, String group, String version, Object service, Class interfaceClass) {
        this.serviceName = serviceName;
        this.group = group;
        this.version = version;
        this.service = service;
        this.interfaceClass = interfaceClass;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }

    public Class getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class interfaceClass) {
        this.interfaceClass = interfaceClass;
    }
}
