package com.jkys.phobos.netty.listener;

/**
 * Created by zdj on 2016/7/8.
 */
public class PhobosChannelActiveListener implements PhobosListener<PhobosChannelActiveEvent> {

    public void onPhobosEvent(PhobosChannelActiveEvent event) {
        System.out.println("PhobosChannelActiveListener");
    }

    public Class<PhobosChannelActiveEvent> getEventClass() {
        return PhobosChannelActiveEvent.class;
    }
}
