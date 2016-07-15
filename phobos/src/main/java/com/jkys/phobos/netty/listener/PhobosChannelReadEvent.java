package com.jkys.phobos.netty.listener;

import com.jkys.phobos.client.InvokeInfo;

/**
 * Created by zdj on 2016/7/15.
 */
public class PhobosChannelReadEvent extends PhobosEvent{

    private final InvokeInfo invokeInfo;

    public PhobosChannelReadEvent(Object source,InvokeInfo invokeInfo) {
        super(source);
        this.invokeInfo = invokeInfo;
    }

    public InvokeInfo getInvokeInfo() {
        return invokeInfo;
    }
}
