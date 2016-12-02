package com.jkys.phobos.netty.listener;

import java.util.EventObject;

/**
 * Created by zdj on 2016/7/8.
 */
public abstract class PhobosEvent extends EventObject {

    public PhobosEvent(Object source) {
        super(source);
    }
}
