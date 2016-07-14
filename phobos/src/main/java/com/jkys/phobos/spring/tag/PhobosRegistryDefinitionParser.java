package com.jkys.phobos.spring.tag;

import com.jkys.phobos.spring.server.PhobosApplicationListener;
import com.jkys.phobos.server.PhobosContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by zdj on 2016/7/4.
 * 解析自定义phobos registry标签
 */
public class PhobosRegistryDefinitionParser implements BeanDefinitionParser {

    public BeanDefinition parse(Element element, ParserContext parserContext) {

        String xbusAddr = element.getAttribute("xbusAddr");
        Integer port = Integer.valueOf(element.getAttribute("servicePort"));
        boolean blocking = Boolean.valueOf(element.getAttribute("blocking"));
        String serverName = element.getAttribute("serverName");
        if(StringUtils.isEmpty(xbusAddr)) throw new NullPointerException("phobos registry tag attribte xbusAddr is empty");
        String[] s = xbusAddr.split(",");

        PhobosContext phobosContext = PhobosContext.getInstance();
        phobosContext.setPort(port);
        for(String addr : s){
            phobosContext.setXbus(addr);
        }

        phobosContext.setBlocking(blocking);
        phobosContext.setServerName(serverName);

        //容器初始化完成后启动netty服务端监听器
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(PhobosApplicationListener.class);
        parserContext.getRegistry().registerBeanDefinition("phobosApplicationListener", beanDefinition);
        return beanDefinition;
    }
}
