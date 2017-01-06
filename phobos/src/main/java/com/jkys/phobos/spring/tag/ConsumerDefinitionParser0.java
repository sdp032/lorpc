package com.jkys.phobos.spring.tag;

import com.jkys.phobos.client.PhobosClientContext;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.spring.client.listener.PhobosClientListener;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Created by zdj on 2016/7/6.
 */
public class ConsumerDefinitionParser0 implements BeanDefinitionParser {

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        PhobosClientContext phobosClientContext = PhobosClientContext.getInstance();
        String clientAppName = element.getAttribute("clientAppName");
        String serialization = element.getAttribute("serialization");
        String keystorePath = element.getAttribute("keystorePath");
        String keystorePassword = element.getAttribute("keystorePassword");
        Integer startTimeOut = Integer.valueOf(element.getAttribute("startTimeOut"));
        Integer requestTimeOut = Integer.valueOf(element.getAttribute("requestTimeOut"));

        phobosClientContext.setClientAppName(clientAppName);
        phobosClientContext.setKeystorePath(keystorePath);
        phobosClientContext.setKeystorePassword(keystorePassword);
        phobosClientContext.setStartTimeOut(startTimeOut);
        phobosClientContext.setRequestTimeOut(requestTimeOut);

        if ("JSON".equals(serialization))
            phobosClientContext.setSerializationType(Header.SerializationType.JSON.serializationType);
        else if ("MAGPACK".equals(serialization))
            phobosClientContext.setSerializationType(Header.SerializationType.MAGPACK.serializationType);
        else if ("PROTOBUFF".equals(serialization))
            phobosClientContext.setSerializationType(Header.SerializationType.PROTOBUFF.serializationType);

        //spring容器初始化完成后启动netty客户端监听
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(PhobosClientListener.class);
        parserContext.getRegistry().registerBeanDefinition("phobosClientListener", beanDefinition);
        return beanDefinition;
    }
}
