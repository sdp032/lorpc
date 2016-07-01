package com.jkys.phobos.spring;

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

        Object testService = context.getBean("test");

        System.out.print(testService.getClass());

        //testService.test();
    }
}
