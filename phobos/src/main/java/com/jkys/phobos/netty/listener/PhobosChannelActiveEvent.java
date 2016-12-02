package com.jkys.phobos.netty.listener;

import com.jkys.phobos.client.InvokeInfo;
import com.jkys.phobos.netty.NettyClient;

/**
 * Created by zdj on 2016/7/8.
 */
public class PhobosChannelActiveEvent extends PhobosEvent {

    private final InvokeInfo invokeInfo;

    public PhobosChannelActiveEvent(NettyClient source, InvokeInfo invokeInfo) {
        super(source);
        this.invokeInfo = invokeInfo;
    }

    public InvokeInfo getInvokeInfo() {
        return invokeInfo;
    }
}
