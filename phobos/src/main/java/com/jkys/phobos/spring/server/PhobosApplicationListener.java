package com.jkys.phobos.spring.server;

import com.github.infrmods.xbus.client.TLSInitException;
import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.client.XbusConfig;
import com.github.infrmods.xbus.exceptions.XBusException;
import com.github.infrmods.xbus.item.ServiceDesc;
import com.github.infrmods.xbus.item.ServiceEndpoint;
import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.netty.NettyServer;
import com.jkys.phobos.server.PhobosContext;
import com.jkys.phobos.server.ServiceBean;
import com.jkys.phobos.util.TypeUtil;
import com.jkys.phobos.util.yaml.BeanRepresenter;
import com.jkys.phobos.util.yaml.Yaml;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Created by zdj on 2016/7/5.
 * 监听spring容器初始化完成事件
 */
public class PhobosApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {


        PhobosContext phobosContext = PhobosContext.getInstance();
        final Integer port = phobosContext.getPort();
        Set<Class> serializeSet = phobosContext.getSerializeSet();

        for (Method m : phobosContext.getMethodMap().values()) {
            TypeUtil.getAllSerializeType(serializeSet, m.getReturnType());
            for (Class c : m.getParameterTypes()) {
                TypeUtil.getAllSerializeType(serializeSet, c);
            }
        }

        MsgpackUtil.register(serializeSet);

        ServiceDesc[] serviceDescs = new ServiceDesc[phobosContext.getServiceMap().values().size()];
        Yaml yaml = new Yaml(new BeanRepresenter());
        try {
            for(int i = 0; i < phobosContext.getServiceMap().values().size(); i++){
                ServiceBean service = (ServiceBean)phobosContext.getServiceMap().values().toArray()[i];
                serviceDescs[i] = new ServiceDesc(phobosContext.getServerAppName() + "." + service.getServiceName(), service.getVersion(), service.getServiceName(), yaml.dump(service.getInterfaceClass()));
            }
        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        phobosContext.setServiceDescs(serviceDescs);

        //启动netty服务
        if (phobosContext.isBlocking()) {
            try {
                new NettyServer(port).open();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        } else {
            new NettyServer(port).noBlockOpen();
        }
    }
}
