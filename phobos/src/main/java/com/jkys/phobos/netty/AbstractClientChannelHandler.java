package com.jkys.phobos.netty;

import com.jkys.phobos.netty.listener.PhobosEvent;
import com.jkys.phobos.netty.listener.PhobosListener;
import io.netty.channel.ChannelHandlerAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zdj on 2016/7/8.
 */
public abstract class AbstractClientChannelHandler extends ChannelHandlerAdapter {

    private boolean isActive = false;

    private List<PhobosListener> listeners = new ArrayList();

    public void addPhobosListener(PhobosListener listener){
        listeners.add(listener);
    }

    public void removePhobosListener(PhobosListener listener){
        listeners.remove(listener);
    }

    public void notify(PhobosEvent event){
        Iterator<PhobosListener> iterator = listeners.iterator();
        while (iterator.hasNext()){
            PhobosListener listener = iterator.next();
            if(event.getClass() == listener.getEventClass()){
                listener.onPhobosEvent(event);
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
