package com.jkys.phobos;


import org.msgpack.annotation.Message;

/**
 * Created by zdj on 2016/7/18.
 */
public class House {

    private User landlord;

    public User getLandlord() {
        return landlord;
    }

    public void setLandlord(User landlord) {
        this.landlord = landlord;
    }
}
