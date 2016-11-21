package com.jkys.phobos;

import org.msgpack.annotation.Message;

import java.util.List;

/**
 * Created by zdj on 2016/7/18.
 */
public class User {

    private String name;

    private Integer age;

    private House house;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
