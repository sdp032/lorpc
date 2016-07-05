package com.jkys.phobos.spring.tag;

import com.jkys.phobos.service.TestService;
import com.jkys.phobos.spring.server.PhobosContext;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zdj on 2016/7/4.
 */
public class TestPhobosServiceDefinitionParser {

    @Test
    public void createService(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application.xml");

        TestService testService = (TestService)context.getBean("testService");
        testService.test();

        PhobosContext phobosContext = PhobosContext.getInstance();
        phobosContext.getMethod("TestService","test","group1","V1.0");
    }
}
