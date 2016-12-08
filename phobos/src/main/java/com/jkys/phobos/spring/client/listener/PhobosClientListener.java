package com.jkys.phobos.spring.client.listener;

import com.github.infrmods.xbus.client.TLSInitException;
import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.client.XbusConfig;
import com.github.infrmods.xbus.exceptions.XBusException;
import com.github.infrmods.xbus.item.Service;
import com.github.infrmods.xbus.item.ServiceEndpoint;
import com.jkys.phobos.annotation.PhobosGroup;
import com.jkys.phobos.annotation.PhobosVersion;
import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.netty.NettyClient;
import com.jkys.phobos.server.PhobosContext;
import com.jkys.phobos.spring.client.beans.PhobosFactoryBean;
import com.jkys.phobos.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by zdj on 2016/7/6.
 */
public class PhobosClientListener implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = LoggerFactory.getLogger(PhobosClientListener.class);

    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        ApplicationContext context = contextRefreshedEvent.getApplicationContext();
        if (context.getParent() != null)
            return;

        //获取所有客户端代理
        Map<String, PhobosFactoryBean> clients = context.getBeansOfType(PhobosFactoryBean.class);

        PhobosClientContext clientContext = PhobosClientContext.getInstance();
        String[] xbusAddrs = clientContext.getXbusAddr();
        Set<String> addr = clientContext.getAddr();
        Set<Class> serializeSet = clientContext.getSerializeSet();
        HashMap<String, List<NettyClient>> connectInfo = clientContext.getConnectInfo();

        Iterator<Map.Entry<String, PhobosFactoryBean>> iterator = clients.entrySet().iterator();
        Set<String> endpoints = new HashSet();
        while (null != iterator && iterator.hasNext()) {
            PhobosFactoryBean factoryBean = iterator.next().getValue();
            Class<?> serviceInterface = factoryBean.getPhobosInterface();
            Method[] serviceMethods = serviceInterface.getMethods();
            String serviceName = serviceInterface.getName();
            for (Method m : serviceMethods) {
                String methodName = m.getName();
                TypeUtil.getAllSerializeType(serializeSet, m.getReturnType());
                for (Class c : m.getParameterTypes()) {
                    TypeUtil.getAllSerializeType(serializeSet, c);
                }

                PhobosVersion version = m.getAnnotation(PhobosVersion.class) == null
                        ? serviceInterface.getAnnotation(PhobosVersion.class)
                        : m.getAnnotation(PhobosVersion.class);

                PhobosGroup group = m.getAnnotation(PhobosGroup.class) == null
                        ? serviceInterface.getAnnotation(PhobosGroup.class)
                        : m.getAnnotation(PhobosGroup.class);
                String key = PhobosContext.generateMethodKey(
                        factoryBean.getServiceAppName() + "." + serviceName,
                        methodName,
                        group.value(),
                        version.version(),
                        m.getParameterTypes()
                        );
                connectInfo.put(key, new ArrayList<NettyClient>());

                //获取xbus上该服务的地址
                try {
                    if (!StringUtils.isEmpty(xbusAddrs)) {
                        XBusClient xBusClient = new XBusClient(new XbusConfig(xbusAddrs, "/home/zdj/Downloads/clitest.ks", "123456"));
                        Service service = xBusClient.getService(factoryBean.getServiceAppName() + "." + serviceName, version.version());
                        if(service.endpoints.length <= 0){
                            logger.error("not find any long distance service in xbus : {}.{}.{}", factoryBean.getServiceAppName(), serviceName, version.version());
                        }
                        for (ServiceEndpoint endpoint : service.endpoints) {
                            endpoints.add(endpoint.address);
                        }
                    }
                } catch (TLSInitException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                } catch (XBusException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        MsgpackUtil.register(serializeSet);

        //创建netty客户端  优先连接xbus上的服务  没有设置xbus时使用直连
        if (endpoints.size() > 0) {
            for (String s : endpoints) {
                try {
                    new NettyClient(s.split(":")[0], Integer.valueOf(s.split(":")[1]), clientContext.getStartTimeOut()).noBlockConnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            for (String s : addr) {
                try {
                    new NettyClient(s.split(":")[0], Integer.valueOf(s.split(":")[1]), clientContext.getStartTimeOut()).noBlockConnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
