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
public class PhobosClientDefinitionParser implements BeanDefinitionParser {

    public BeanDefinition parse(Element element, ParserContext parserContext) {

        PhobosClientContext phobosClientContext = PhobosClientContext.getInstance();
        String xbusAddr = element.getAttribute("xbusAddr");
        String addr = element.getAttribute("addr");
        String clientAppName = element.getAttribute("clientAppName");
        String serialization = element.getAttribute("serialization");

        if(!StringUtils.isEmpty(xbusAddr)){
            for(String s : xbusAddr.split(",")){
                phobosClientContext.getXbusAddr().add(s);
            }
        }
        if(!StringUtils.isEmpty(addr)){
            for (String s : addr.split(",")){
                phobosClientContext.getAddr().add(s);
            }
        }

        phobosClientContext.setClientAppName(clientAppName);

        //TODO 待优化
        if("JSON".equals(serialization))
            phobosClientContext.setSerializationType(Header.SerializationType.JSON.serializationType);
        else if("MAGPACK".equals(serialization))
            phobosClientContext.setSerializationType(Header.SerializationType.MAGPACK.serializationType);
        else if("PROTOBUFF".equals(serialization))
            phobosClientContext.setSerializationType(Header.SerializationType.PROTOBUFF.serializationType);

        //spring容器初始化完成后启动netty客户端监听
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(PhobosClientListener.class);
        parserContext.getRegistry().registerBeanDefinition("phobosClientListener", beanDefinition);
        return beanDefinition;
    }
}
