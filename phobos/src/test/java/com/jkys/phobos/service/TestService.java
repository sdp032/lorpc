package com.jkys.phobos.service;

import com.jkys.phobos.House;
import com.jkys.phobos.annotation.PhobosVersion;
import com.jkys.phobos.annotation.PhobosGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by zdj on 2016/7/1.
 */
@PhobosGroup("group1")
@PhobosVersion(version = "V1.0")
public interface TestService {

    void test();

    House getHouse(House list);

    House getHouse(String[] str, Integer i, boolean b,  Map<String, Integer> m, List<Map<String,String>> list) throws Exception;
}
