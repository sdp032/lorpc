package test;

import com.jkys.phobos.proto.ServiceProto;
import test.service.TestService;
import com.jkys.phobos.spring.server.ServerBean;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URL;

/**
 * Created by zdj on 2016/7/6.
 */
public class TestServer {
    @Test
    public void classTest() {
        ClassLoader loader = ServerBean.class.getClassLoader();
        URL url = loader.getResource("com/jkys/phobos/spring/server/ServerBean.class");
        System.out.println(url);
    }

    @Test
    public void testServer() throws InterruptedException {
        System.out.println(System.getProperty("user.dir"));
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/server-application.xml");
        ServerBean serverBean = (ServerBean) context.getBean(ServerBean.NAME);
        serverBean.joinServer();
    }

    @Test
    public void testShutdownServer() throws InterruptedException {
        System.out.println(System.getProperty("user.dir"));
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/server-application.xml");
        ServerBean serverBean = (ServerBean) context.getBean(ServerBean.NAME);
        new Thread(() -> {
            try {
                Thread.sleep(8000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            System.out.println("to stop server");
            serverBean.stopServer();
        }).start();
        serverBean.joinServer();
    }

    @Test
    public void protoTest() {
        System.out.println(new ServiceProto(TestService.class).toYaml());
    }
}
