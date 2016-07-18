package com.jkys.phobos.service.impl;

import com.jkys.phobos.House;
import com.jkys.phobos.service.TestService;

/**
 * Created by zdj on 2016/7/4.
 */
public class TestServiceImpl implements TestService {

    public void test() {
        System.out.println("hello phobos");
    }

    public House getHouse(House house) {
        System.out.println(house.getLandlord().getName());
        System.out.println(house.getLandlord().getAge());
        return house;
    }
}
