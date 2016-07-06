package com.jkys.phobos.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by zdj on 2016/7/6.
 */
public class PhobosClientContext {

    private HashSet<String> xbusAddr;

    private HashSet<String> addr;

    private HashMap<Class,List<String>> connectInfo;

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
}
