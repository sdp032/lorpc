package test;

import test.service.Person;
import test.service.Result;
import test.service.TestService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zdj on 2016/7/6.
 */
public class TestClient {
    @Test
    public void testClient() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/client-application.xml");

        TestService testService = context.getBean(TestService.class);
        testService.empty();
        System.out.println(testService.hello("service client"));
        Result<Person> result = testService.getPerson("service user");
        System.out.println(result.getResult());
        System.out.println(testService.random());
    }

    @Test
    public void longTest() throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/client-application.xml");

        TestService testService = context.getBean(TestService.class);
        for (int i = 0; i < 100; i++) {
            testService.empty();
            System.out.println("empty");
            Thread.sleep(3000);
        }
    }

    @Test
    public void clientBench() {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/client-application.xml");

        TestService testService = context.getBean(TestService.class);
        int N = 100000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            testService.empty();
        }
        System.out.println("void call qps: " + ((double)N * 1000 / (System.currentTimeMillis() - start)));

        start = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            testService.hello("service user");
        }
        System.out.println("hello call qps: " + ((double)N * 1000 / (System.currentTimeMillis() - start)));

        start = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            testService.getPerson("service user");
        }
        System.out.println("getPerson call qps: " + ((double)N * 1000 / (System.currentTimeMillis() - start)));
    }

    @Test
    public void multiBench() throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/client-application.xml");

        TestService testService = context.getBean(TestService.class);
        List<Thread> threads = new ArrayList<>();
        int threadN = 8;
        int N = 100000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadN; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < N; j++) {
                    testService.getPerson("service user");
                }
            });
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("qps: " + (double)(threadN * N) / ((double)(System.currentTimeMillis() - start) / 1000));
    }

    @Test
    public void xxTest() throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/client-application.xml");

        TestService testService = context.getBean(TestService.class);
        testService.empty();
        Thread.sleep(100 * 1000);
    }
}