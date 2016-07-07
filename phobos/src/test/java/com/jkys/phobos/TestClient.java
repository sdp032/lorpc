package com.jkys.phobos;

import com.jkys.phobos.service.TestService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zdj on 2016/7/6.
 */
public class TestClient {
    @Test
    public void test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/client-application.xml");

        TestService testService = (TestService) context.getBean("test");
        testService.test();
    }
}
