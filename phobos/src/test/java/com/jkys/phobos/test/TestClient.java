package com.jkys.phobos.test;

import com.jkys.phobos.test.service.Person;
import com.jkys.phobos.test.service.Result;
import com.jkys.phobos.test.service.TestService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zdj on 2016/7/6.
 */
public class TestClient {
    @Test
    public void testClient() throws Exception{
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/client-application.xml");

        TestService testService = context.getBean(TestService.class);
        testService.empty();
        System.out.println(testService.hello("service client"));
        Result<Person> result = testService.getPerson("service user");
        System.out.println(result.getResult());
        System.out.println(testService.random());
    }
}