package com.jkys.phobos.netty.listener;

import java.util.EventListener;

/**
 * Created by zdj on 2016/7/8.
 */
public interface PhobosListener<E extends PhobosEvent> extends EventListener {

    void onPhobosEvent(E event) throws Exception;

    Class<E> getEventClass();
}
