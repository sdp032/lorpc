package com.jkys.phobos.service.impl;

import com.jkys.phobos.House;
import com.jkys.phobos.service.TestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zdj on 2016/7/4.
 */
public class TestServiceImpl implements TestService {

    private ArrayList<String> list;

    private HashMap<String, House> map;

    public void test() {
        System.out.println("hello phobos");
    }

 /*   public House getHouse(House house) {
        System.out.println(house.getLandlord().getName());
        System.out.println(house.getLandlord().getAge());
        return house;
    }*/

    public List<Map<String, House[]>> getHouse(String str[], Integer i, boolean b, Map<String, Integer[]> m, List<Map<String, House[]>> list) throws Exception {
        System.out.println(str[1]);
        System.out.print(m.get("1")[1]);
        System.out.println(b);
        System.out.println(list.get(1).get("1")[1]);
        list.get(1).get("1")[1].getLandlord().setName("hah");
        return list;
    }
}
