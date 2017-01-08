package com.jkys.phobos.spring.tag;

import com.jkys.phobos.annotation.ServiceName;
import com.jkys.phobos.annotation.ServiceVersion;
import com.jkys.phobos.spring.client.beans.ClientBean;
import com.jkys.phobos.spring.client.beans.PhobosFactoryBean;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by lo on 1/6/17.
 */
public class ConsumerDefinitionParser implements BeanDefinitionParser {
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Class<?> interfaceCls;
        try {
            interfaceCls = Class.forName(element.getAttribute("interface"));
        } catch (ClassNotFoundException e) {
            // FIXME
            throw new RuntimeException(e);
        }
        if (!interfaceCls.isInterface()) {
            throw new RuntimeException("invalid interface");
        }
        ServiceName name = interfaceCls.getAnnotation(ServiceName.class);
        if (name == null || name.value().equals("")) {
            // FIXME exception
            throw new RuntimeException("invalid service name");
        }
        ServiceVersion version = interfaceCls.getAnnotation(ServiceVersion.class);
        if (version == null || version.version().equals("")) {
            // FIXME exception
            throw new RuntimeException("invalid version");
        }

        if (!parserContext.getRegistry().containsBeanDefinition(ClientBean.NAME)) {
            RootBeanDefinition clientBean = new RootBeanDefinition();
            clientBean.setBeanClass(ClientBean.class);
            parserContext.getRegistry().registerBeanDefinition(ClientBean.NAME, clientBean);
        }

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(PhobosFactoryBean.class);
        MutablePropertyValues values = new MutablePropertyValues();
        values.addPropertyValue("serviceInterface", interfaceCls);
        values.addPropertyValue("serviceName", name.value());
        values.addPropertyValue("serviceVersion", version.version());
        beanDefinition.setPropertyValues(values);
        String id = element.getAttribute("id");
        if (id.equals("")) {
            id = "_phbos_consume_" + name.value() + "_" + version.version();
        }
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        return beanDefinition;
    }
}
