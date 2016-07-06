package com.jkys.phobos.spring.client.listener;

import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.netty.NettyClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Set;

/**
 * Created by zdj on 2016/7/6.
 */
public class PhobosClientListener implements ApplicationListener<ContextRefreshedEvent> {

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        PhobosClientContext clientContext = PhobosClientContext.getInstance();
        Set<String> xbusAddr = clientContext.getXbusAddr();
        Set<String> addr = clientContext.getAddr();

        //TODO 获取xbus上符合条件的服务地址

        //创建netty客户端
        for(String s : addr){
           try{
               new NettyClient(s.split(":")[0],Integer.valueOf(s.split(":")[1]));
           }catch (Exception e){
               System.err.println("addr format is wrong : " + s);
           }
        }

    }
}
