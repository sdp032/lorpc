package com.jkys.phobos;

import com.jkys.phobos.service.TestService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zdj on 2016/7/6.
 */
public class TestClient {
    @Test
    public void test() throws Exception{
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/client-application.xml");

        TestService testService = (TestService) context.getBean("test");
        //testService.test();
        String[] s = new String[]{"s", "i am string[]"};
        Map<String, Integer[]> map = new HashMap<>();
        map.put("1", new Integer[]{0,1});
        List<Map<String, House[]>> list = new ArrayList<>();
        Map<String,House[]> m = new HashMap<>();
        House h = new House();
        h.setLandlord(new User());
        m.put("1", new House[]{null,h});
        list.add(null);
        list.add(m);
        List<Map<String, House[]>> r = testService.getHouse(null, 1, false, map, list);
        System.out.println("****************"+r.get(1).get("1")[1].getLandlord().getName());
    }
}