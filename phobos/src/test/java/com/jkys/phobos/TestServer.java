package com.jkys.phobos;

import com.jkys.phobos.spring.server.ServerBean;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zdj on 2016/7/6.
 */
public class TestServer {

    @Test
    public void test() throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/server-application.xml");
        ServerBean serverBean = (ServerBean) context.getBean(ServerBean.NAME);
        serverBean.joinServer();
    }
}
