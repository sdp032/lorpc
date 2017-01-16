package com.jkys.phobos.spring.tag;

import com.jkys.phobos.annotation.Service;
import com.jkys.phobos.annotation.ServiceUtil;
import com.jkys.phobos.client.ClientContext;
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
        Service service = interfaceCls.getAnnotation(Service.class);
        if (service == null) {
            throw new RuntimeException("missing ServiceProto");
        }
        String[] nameVersion = ServiceUtil.splitServiceKey(service);

        String address = element.getAttribute("address");
        if (address != null && !address.equals("")) {
            ClientContext.getInstance().presetAddress(nameVersion[0], nameVersion[1], address);
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
        values.addPropertyValue("serviceName", nameVersion[0]);
        values.addPropertyValue("serviceVersion", nameVersion[1]);
        beanDefinition.setPropertyValues(values);
        String id = element.getAttribute("id");
        if (id.equals("")) {
            id = "_phbos_consume_" + nameVersion[0] + "_" + nameVersion[1];
        }
        parserContext.getRegistry().registerBeanDefinition(id, beanDefinition);
        return beanDefinition;
    }
}
