package com.jkys.phobos.client;

import com.jkys.phobos.remote.protocol.Header;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Handler;

/**
 * Created by zdj on 2016/7/6.
 */
public class PhobosClientContext {

    private String clientAppName;

    private char serializationType;

    private HashSet<String> xbusAddr = new HashSet();

    private HashSet<String> addr = new HashSet();

    private HashMap<Class,List<String>> connectInfo = new HashMap();

    private int startTimeOut;

    private int requestTimeOut;

    private Set<Class> serializeSet = new HashSet();

    private static PhobosClientContext phobosClientContext = null;

    private PhobosClientContext(){}

    public synchronized static PhobosClientContext getInstance(){
        if(phobosClientContext == null){
            phobosClientContext = new PhobosClientContext();
        }
        return phobosClientContext;
    }

    public HashSet<String> getXbusAddr() {
        return xbusAddr;
    }

    public void setXbusAddr(HashSet<String> xbusAddr) {
        this.xbusAddr = xbusAddr;
    }

    public HashSet<String> getAddr() {
        return addr;
    }

    public void setAddr(HashSet<String> addr) {
        this.addr = addr;
    }

    public HashMap<Class, List<String>> getConnectInfo() {
        return connectInfo;
    }

    public void setConnectInfo(HashMap<Class, List<String>> connectInfo) {
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
}
