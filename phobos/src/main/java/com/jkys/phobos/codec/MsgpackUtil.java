package com.jkys.phobos.codec;

import org.msgpack.MessagePack;
import org.msgpack.template.TemplateRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by zdj on 2016/7/13.
 */
public class MsgpackUtil {

    private final static Logger logger = LoggerFactory.getLogger(MsgpackUtil.class);

    public static final MessagePack MESSAGE_PACK = new MessagePack();

    private static MyJavassistTemplateBuilder builder;

    static {
        try {
            Field field = MessagePack.class.getDeclaredField("registry");
            field.setAccessible(true);
            TemplateRegistry registry = (TemplateRegistry) field.get(MESSAGE_PACK);
            builder = new MyJavassistTemplateBuilder(registry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void register(Set<Class> set) {

        List<Class> list = new ArrayList();
        list.addAll(set);

        while (list.size() > 0) {
            try {
                //MESSAGE_PACK.register(list.get(0));
                builder.buildAndRegister(list.get(0));
                Class c = list.remove(0);
                logger.info("register type --> {}", c.getName());
            } catch (Exception e) {
                list.add(list.remove(0));
            }
        }

    }

}
