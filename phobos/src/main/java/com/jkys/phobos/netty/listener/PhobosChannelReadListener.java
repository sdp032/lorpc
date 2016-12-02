package com.jkys.phobos.netty.listener;

import com.jkys.phobos.client.InvokeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zdj on 2016/7/15.
 */
public class PhobosChannelReadListener implements PhobosListener<PhobosChannelReadEvent> {

    private static Logger logger = LoggerFactory.getLogger(PhobosChannelReadListener.class);

    public void onPhobosEvent(PhobosChannelReadEvent event) throws Exception {

        InvokeInfo invokeInfo = event.getInvokeInfo();
        if (invokeInfo == null) {
            throw new NullPointerException("invokeInfo is null");
        }
        synchronized (invokeInfo) {
            invokeInfo.setTimeOut(false);
            invokeInfo.notify();
        }
    }

    public Class<PhobosChannelReadEvent> getEventClass() {
        return PhobosChannelReadEvent.class;
    }
}
