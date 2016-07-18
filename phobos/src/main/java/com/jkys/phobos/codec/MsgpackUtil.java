package com.jkys.phobos.codec;

import org.msgpack.MessagePack;

import java.util.*;

/**
 * Created by zdj on 2016/7/13.
 */
public class MsgpackUtil {

    public static final MessagePack MESSAGE_PACK =  new MessagePack();

    public static void register(Set<Class> set){

        List<Class> list = new ArrayList();
        list.addAll(set);

        while (list.size()>0){
            try {
                MESSAGE_PACK.register(list.get(0));
                Class c = list.remove(0);
                System.out.println("register type " + c.getName() + " for MessagePack");
            }catch (Exception e){
                list.add(list.remove(0));
            }
        }

    }

}
