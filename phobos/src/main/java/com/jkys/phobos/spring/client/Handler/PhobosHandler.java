package com.jkys.phobos.spring.client.Handler;

import java.lang.reflect.Method;

/**
 * Created by zdj on 2016/7/1.
 */
public interface PhobosHandler{

    Object execution(Method method,Object[] args) throws Exception;
}
