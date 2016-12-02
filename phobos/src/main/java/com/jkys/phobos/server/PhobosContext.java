package com.jkys.phobos.server;

import com.github.infrmods.xbus.item.ServiceDesc;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zdj on 2016/7/4.
 * <p>
 * 服务上下文 保存服务端注册信息、服务方法存储   该类为单例
 */
public class PhobosContext {

    private static PhobosContext phobosContext = null;

    /**
     * key = serviceName.methodName.group.version
     * value = Method
     */
    private ConcurrentHashMap<String, Method> methodMap = new ConcurrentHashMap();

    /**
     * key = serviceName.group.version
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
     * xbus描述文件
     */
    private ServiceDesc[] serviceDescs;

    /**
     * 服务端口号
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

    private String serverAppName;

    private PhobosContext() {
    }

    public synchronized static PhobosContext getInstance() {
        if (phobosContext == null) {
            phobosContext = new PhobosContext();
        }
        return phobosContext;
    }

    public Method getMethod(String serviceName, String methodName, String group, String version, Class<?>[] params) {
        return methodMap.get(generateMethodKey(serviceName, methodName, group, version, params));
    }

    public Method getMethod(String key){
        return methodMap.get(key);
    }

    public ServiceBean getService(String serviceName, String group, String version) {
        return this.serviceMap.get(this.generateServiceKey(serviceName, group, version));
    }

    public ConcurrentHashMap<String, Method> getMethodMap() {
        return methodMap;
    }

    public void setMethod(String serviceName, String methodName, String group, String version, Class<?>[] params, Method method) {
        methodMap.put(generateMethodKey(serviceName, methodName, group, version, params), method);
    }

    public ConcurrentHashMap<String, ServiceBean> getServiceMap() {
        return this.serviceMap;
    }

    public void setService(String serviceName, String group, String version, ServiceBean service) {
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

    public String getServerAppName() {
        return serverAppName;
    }

    public void setServerAppName(String serverAppName) {
        this.serverAppName = serverAppName;
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

    public ServiceDesc[] getServiceDescs() {
        return serviceDescs;
    }

    public void setServiceDescs(ServiceDesc[] serviceDescs) {
        this.serviceDescs = serviceDescs;
    }

    public static String generateMethodKey(String serviceName, String methodName, String group, String version, Class<?>[] params) {
        StringBuffer sb = new StringBuffer();
        sb.append(serviceName);
        sb.append(".");
        sb.append(methodName);
        sb.append("(");
        if(params != null){
            for (int i=0; i<params.length; i++){
                sb.append(params[i].getName());
                if(i < params.length -1){
                    sb.append(",");
                }
            }
        }
        sb.append(")");
        sb.append(".");
        sb.append(group);
        sb.append(".");
        sb.append(version);
        return sb.toString();
    }

    public static String generateServiceKey(String serviceName, String group, String version) {
        StringBuffer sb = new StringBuffer();
        sb.append(serviceName);
        sb.append(".");
        sb.append(group);
        sb.append(".");
        sb.append(version);
        return sb.toString();
    }
}
