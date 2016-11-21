package com.jkys.phobos;

import com.jkys.phobos.service.TestService;
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
    public void test(){
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/client-application.xml");

        TestService testService = (TestService) context.getBean("test");
        //testService.test();

        User u = new User();
        u.setAge(22);
        u.setName("zzz");
        House house = new House();
        house.setLandlord(u);
        List<House> list = new ArrayList();
        list.add(house);
        house = testService.getHouse(house);
        System.out.println(house);
    }
}
