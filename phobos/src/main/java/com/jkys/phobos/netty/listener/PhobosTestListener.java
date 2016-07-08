package com.jkys.phobos.netty.listener;

/**
 * Created by zdj on 2016/7/8.
 */
public class PhobosTestListener implements PhobosListener<TestEvent> {

    public void onPhobosEvent(TestEvent event) {
        System.out.println("test");
    }

    public Class<TestEvent> getEventClass() {
        return TestEvent.class;
    }
}
