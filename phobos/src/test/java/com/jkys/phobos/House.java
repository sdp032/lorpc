package com.jkys.phobos;


import org.msgpack.annotation.Message;

/**
 * Created by zdj on 2016/7/18.
 */
@Message
public class House<T> {

    T t;

    private User landlord;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public User getLandlord() {
        return landlord;
    }

    public void setLandlord(User landlord) {
        this.landlord = landlord;
    }
}
