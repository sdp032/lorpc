package com.jkys.phobos;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zdj on 2016/7/6.
 */
public class TestServer {

    @Test
    private void test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/server-application.xml");

    }
}
