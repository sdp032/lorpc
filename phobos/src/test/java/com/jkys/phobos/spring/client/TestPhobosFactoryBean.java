package com.jkys.phobos.spring.client;

import com.jkys.phobos.service.TestService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zdj on 2016/7/1.
 */
public class TestPhobosFactoryBean {

    @Test
    public void createrBean(){

        ApplicationContext context = new ClassPathXmlApplicationContext("spring/application.xml");

        TestService testService = (TestService)context.getBean("test");

        testService.test();
    }
}
