package com.jkys.phobos.spring.client.listener;

import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.netty.NettyClient;
import com.jkys.phobos.spring.client.beans.PhobosFactoryBean;
import com.jkys.phobos.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by zdj on 2016/7/6.
 */
    public class PhobosClientListener implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = LoggerFactory.getLogger(PhobosClientListener.class);

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        ApplicationContext context = contextRefreshedEvent.getApplicationContext();
        if(context.getParent() != null)
            return;

        //获取所有客户端代理
        Map<String,PhobosFactoryBean> clients = context.getBeansOfType(PhobosFactoryBean.class);

        PhobosClientContext clientContext = PhobosClientContext.getInstance();
        Set<String> xbusAddr = clientContext.getXbusAddr();
        Set<String> addr = clientContext.getAddr();
        HashMap<Class,List<String>> connectInfo = clientContext.getConnectInfo();
        Set<Class> serializeSet = clientContext.getSerializeSet();

        Iterator<Map.Entry<String,PhobosFactoryBean>> iterator = clients.entrySet().iterator();
        while (null!=iterator && iterator.hasNext()){
            Class serviceInterface = iterator.next().getValue().getPhobosInterface();
            Method[] serviceMethods = serviceInterface.getMethods();
            for (Method m : serviceMethods){
                TypeUtil.getAllSerializeType(serializeSet,m.getReturnType());
                for(Class c : m.getParameterTypes()){
                    TypeUtil.getAllSerializeType(serializeSet,c);
                }
            }

            connectInfo.put(serviceInterface,new ArrayList<String>());
        }

        MsgpackUtil.register(serializeSet);

        //TODO 获取xbus上符合条件的服务地址

        //创建netty客户端
        for(String s : addr){
           try{
               new NettyClient(s.split(":")[0],Integer.valueOf(s.split(":")[1]),clientContext.getStartTimeOut()).connect();
           }catch (Exception e){
               e.printStackTrace();
           }
        }
    }
}
