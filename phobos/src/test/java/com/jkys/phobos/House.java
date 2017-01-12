package com.jkys.phobos;


import org.msgpack.annotation.Message;

/**
 * Created by zdj on 2016/7/18.
 */
@Message
public class House {

    User t;

    private User landlord;

    public User getT() {
        return t;
    }

    public void setT(User t) {
        this.t = t;
    }

    public User getLandlord() {
        return landlord;
    }

    public void setLandlord(User landlord) {
        this.landlord = landlord;
    }
}
