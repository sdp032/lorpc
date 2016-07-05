package com.jkys.phobos.spring.tag;

import com.jkys.phobos.spring.server.PhobosContext;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Set;

/**
 * Created by zdj on 2016/7/4.
 */
public class TestPhobosRegistryDefinitionParser {

    @Test
    public void registry(){

        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application.xml");

        PhobosContext phobosContext = PhobosContext.getInstance();
        Set<String> xbusSet = phobosContext.getXbusSet();
        for (String addr : xbusSet){
            System.out.println(addr);
        }
    }
}
