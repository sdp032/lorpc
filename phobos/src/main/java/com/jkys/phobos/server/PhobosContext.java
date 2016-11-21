package com.jkys.phobos.server;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zdj on 2016/7/4.
 *
 * 服务上下文 保存服务端注册信息、服务方法存储   该类为单例
 */
public class PhobosContext {

    private static PhobosContext phobosContext = null;

    /**
     * key = serviceName_methodName_group_version
     * value = Method
     */
    private ConcurrentHashMap<String,Method> methodMap = new ConcurrentHashMap();

    /**
     * key = serviceName_group_version
     * value = Service
     */
    private ConcurrentHashMap<String, ServiceBean> serviceMap = new ConcurrentHashMap();

    /**
     * xbus注册地址集合
     */
    private String[] xbusAddrs;

    /**
     * xbus keystore路径
     */
    private String keystorePath;

    /**
     * xbus keystore 密码
     */
    private String keystorePassword;

    /**
     *  服务端口号
     */
    private Integer port;

    /**
     * 服务是否阻塞
     */
    private boolean blocking;

    /**
     * 需要进行序列化的类型集合 （服务参数类型及返回值类型）
     */
    private Set<Class> serializeSet = new HashSet();

    private String serverName;

    private PhobosContext(){}

    public synchronized static PhobosContext getInstance(){
        if(phobosContext == null){
            phobosContext = new PhobosContext();
        }
        return  phobosContext;
    }

    public Method getMethod(String serviceName,String methodName,String group,String version){
        return methodMap.get(generateMethodKey(serviceName,methodName,group,version));
    }

    public ServiceBean getService(String serviceName,String group,String version){
        return this.serviceMap.get(this.generateServiceKey(serviceName, group, version));
    }

    public ConcurrentHashMap<String, Method> getMethodMap() {
        return methodMap;
    }

    public void setMethod(String serviceName, String methodName, String group, String version, Method method){
        methodMap.put(generateMethodKey(serviceName,methodName,group,version),method);
    }

    public ConcurrentHashMap<String, ServiceBean> getServiceMap(){
        return this.serviceMap;
    }

    public void setService(String serviceName, String group, String version, ServiceBean service){
        this.serviceMap.put(this.generateServiceKey(serviceName, group, version), service);
    }

    public String[] getXbusAddrs() {
        return xbusAddrs;
    }

    public void setXbusAddrs(String[] xbusAddrs) {
        this.xbusAddrs = xbusAddrs;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public Set<Class> getSerializeSet() {
        return serializeSet;
    }

    public void setSerializeSet(Set<Class> serializeSet) {
        this.serializeSet = serializeSet;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public void setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
    }

    private String generateMethodKey(String serviceName, String methodName, String group, String version){
        StringBuffer sb = new StringBuffer();
        sb.append(serviceName);
        sb.append("_");
        sb.append(methodName);
        sb.append("_");
        sb.append(group);
        sb.append("_");
        sb.append(version);
        return sb.toString();
    }

    private String generateServiceKey(String serviceName, String group, String version){
        StringBuffer sb = new StringBuffer();
        sb.append(serviceName);
        sb.append("_");
        sb.append(group);
        sb.append("_");
        sb.append(version);
        return sb.toString();
    }
}
