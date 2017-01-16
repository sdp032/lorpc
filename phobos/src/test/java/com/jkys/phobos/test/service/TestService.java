package com.jkys.phobos.test.service;

import com.jkys.phobos.annotation.Service;

/**
 * Created by lo on 1/16/17.
 */
@Service("clitest.TestService:1.0")
public interface TestService {
    void empty();
    String hello(String name);
    Result<Person> getPerson(String name);
    Result<Person> random();
}
