package com.jkys.phobos.service;

import com.jkys.phobos.House;
import com.jkys.phobos.annotation.Param;
import com.jkys.phobos.annotation.ServiceName;
import com.jkys.phobos.annotation.ServiceVersion;

import java.util.List;
import java.util.Map;

/**
 * Created by zdj on 2016/7/1.
 */
@ServiceName("clitest.test-service")
@ServiceVersion(version = "V2.5")
public interface TestService {

    String hello(String name);

    /*House getHouse(House list);*/

    List<Map<String, House[]>> getHouse(@Param(name = "str") String[] str, @Param(name = "int") Integer i, @Param(name = "bool") boolean b, @Param(name = "map") Map<String, Integer[]> m, @Param(name = "list") List<Map<String, House[]>> list) throws Exception;
}
