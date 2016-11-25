package com.jkys.phobos.client;

import com.jkys.phobos.netty.NettyClient;
import com.jkys.phobos.remote.protocol.Header;

import java.util.*;
import java.util.logging.Handler;

/**
 * Created by zdj on 2016/7/6.
 */
public class PhobosClientContext {

    private String clientAppName;

    private char serializationType;

    private String[] xbusAddr;

    private HashSet<String> addr = new HashSet();

    private HashMap<String,List<NettyClient>> connectInfo = new HashMap();

    private int startTimeOut;

    private int requestTimeOut;

    private Set<Class> serializeSet = new HashSet();

    private Map<Long,InvokeInfo> invokeInfoMap = new HashMap();

    private static PhobosClientContext phobosClientContext = null;

    private PhobosClientContext(){}

    public synchronized static PhobosClientContext getInstance(){
        if(phobosClientContext == null){
            phobosClientContext = new PhobosClientContext();
        }
        return phobosClientContext;
    }

    public String[] getXbusAddr() {
        return xbusAddr;
    }

    public void setXbusAddr(String[] xbusAddr) {
        this.xbusAddr = xbusAddr;
    }

    public HashSet<String> getAddr() {
        return addr;
    }

    public void setAddr(HashSet<String> addr) {
        this.addr = addr;
    }

    public HashMap<String, List<NettyClient>> getConnectInfo() {
        return connectInfo;
    }

    public void setConnectInfo(HashMap<String, List<NettyClient>> connectInfo) {
        this.connectInfo = connectInfo;
    }

    public String getClientAppName() {
        return clientAppName;
    }

    public void setClientAppName(String clientAppName) {
        this.clientAppName = clientAppName;
    }

    public char getSerializationType() {
        return serializationType;
    }

    public void setSerializationType(char serializationType) {
        this.serializationType = serializationType;
    }

    public int getStartTimeOut() {
        return startTimeOut;
    }

    public void setStartTimeOut(int startTimeOut) {
        this.startTimeOut = startTimeOut;
    }

    public int getRequestTimeOut() {
        return requestTimeOut;
    }

    public void setRequestTimeOut(int requestTimeOut) {
        this.requestTimeOut = requestTimeOut;
    }

    public Set<Class> getSerializeSet() {
        return serializeSet;
    }

    public void setSerializeSet(Set<Class> serializeSet) {
        this.serializeSet = serializeSet;
    }

    public InvokeInfo getInvokeInfo(Long key){
        return invokeInfoMap.get(key);
    }

    public InvokeInfo removeInvokeInfo(Long key){
        return invokeInfoMap.remove(key);
    }

    public void setInvokeInfo (InvokeInfo invokeInfo){
        if(invokeInfo == null)
            throw new NullPointerException("invokeInfo is null");
        if(invokeInfo.getRequest() == null)
            throw new NullPointerException("PhobosRequest is null");
        invokeInfoMap.put(invokeInfo.getRequest().getHeader().getSequenceId(),invokeInfo);
    }
}
