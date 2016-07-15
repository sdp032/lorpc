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

    private List<PhobosListener> listeners = new ArrayList();

    private NettyClient source;

    public AbstractClientChannelHandler(NettyClient source){
        this.source = source;
    }

    public AbstractClientChannelHandler addPhobosListener(PhobosListener listener){
        listeners.add(listener);
        return this;
    }

    public AbstractClientChannelHandler removePhobosListener(PhobosListener listener){
        listeners.remove(listener);
        return this;
    }

    public void notify(PhobosEvent event) throws Exception{
        Iterator<PhobosListener> iterator = listeners.iterator();
        while (iterator.hasNext()){
            PhobosListener listener = iterator.next();
            if(event.getClass() == listener.getEventClass()){
                listener.onPhobosEvent(event);
            }
        }
    }

    public NettyClient getSource() {
        return source;
    }

    public void setSource(NettyClient source) {
        this.source = source;
    }
}
