package com.jkys.phobos;

import com.jkys.phobos.proto.ServiceProto;
import com.jkys.phobos.service.TestService;
import com.jkys.phobos.spring.server.ServerBean;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zdj on 2016/7/6.
 */
public class TestServer {

    @Test
    public void testServer() throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/server-application.xml");
        ServerBean serverBean = (ServerBean) context.getBean(ServerBean.NAME);
        serverBean.joinServer();
    }

    @Test
    public void protoTest() {
        System.out.println(new ServiceProto(TestService.class).toYaml());
    }
}
