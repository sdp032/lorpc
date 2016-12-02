package com.jkys.phobos.util.yaml;

/**
 * Created by zdj on 2016/11/18.
 */
public interface Representer<T> {

    String represent(T t) throws Exception;
}
