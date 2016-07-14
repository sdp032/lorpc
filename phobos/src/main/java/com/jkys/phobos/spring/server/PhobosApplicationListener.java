package com.jkys.phobos.spring.server;

import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.netty.NettyServer;
import com.jkys.phobos.server.PhobosContext;
import com.jkys.phobos.util.TypeUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by zdj on 2016/7/5.
 * 监听spring容器初始化完成事件
 */
public class PhobosApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {


        PhobosContext phobosContext = PhobosContext.getInstance();
        final Integer port = phobosContext.getPort();
        Set<String> xbusAddr = phobosContext.getXbusSet();
        Set<Class> serializeSet = phobosContext.getSerializeSet();

        for(Method m : phobosContext.getMethodMap().values()){
            TypeUtil.getAllSerializeType(serializeSet,m.getReturnType());
            for(Class c : m.getParameterTypes()){
                TypeUtil.getAllSerializeType(serializeSet,c);
            }
        }

        MsgpackUtil.register(serializeSet);

        //启动netty服务
        if(phobosContext.isBlocking()) {
            try {
                new NettyServer(port).open();
            }catch (Exception e){
                e.printStackTrace();
                System.exit(0);
            }
        }else{
            new NettyServer(port).noBlockOpen();
        }

        //TODO 向xbus注册服务
    }
}
