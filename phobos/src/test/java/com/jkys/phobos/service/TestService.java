package com.jkys.phobos.service;

import com.jkys.phobos.annotation.PhobosAddr;
import com.jkys.phobos.annotation.PhobosVersion;

/**
 * Created by zdj on 2016/7/1.
 */
@PhobosAddr(addr = {"localhost:8080"})
public interface TestService {

    @PhobosVersion(version = "V1.0")
    void test();
}
