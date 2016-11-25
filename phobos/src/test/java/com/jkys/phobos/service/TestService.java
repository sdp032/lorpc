package com.jkys.phobos.service;

import com.jkys.phobos.House;
import com.jkys.phobos.annotation.Param;
import com.jkys.phobos.annotation.PhobosVersion;
import com.jkys.phobos.annotation.PhobosGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by zdj on 2016/7/1.
 */
@PhobosGroup("group1")
@PhobosVersion(version = "V2.0")
public interface TestService {

    void test();

    House getHouse(House list);

    House getHouse(@Param(name = "str") String str, @Param(name = "int") Integer i, @Param(name = "bool") boolean b, @Param(name = "map") Map<String, Integer> m, @Param(name = "list") List<Map<String,String>> list) throws Exception;
}
