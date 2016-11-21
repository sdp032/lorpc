package com.jkys.phobos;

import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.client.XbusConfig;
import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.codec.MyJavassistTemplateBuilder;
import com.jkys.phobos.netty.AbstractServerChannelHandler;
import com.jkys.phobos.netty.DefaultServerChannelHandler;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.Request;
import com.jkys.phobos.service.TestService;
import com.jkys.phobos.service.impl.TestServiceImpl;
import com.jkys.phobos.util.TypeUtil;
import com.jkys.phobos.util.yaml.BeanRepresenter;
import com.jkys.phobos.util.yaml.Yaml;
import javassist.ClassPool;
import javassist.CtClass;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;
import org.msgpack.template.*;
import org.msgpack.template.builder.TemplateBuilder;
import org.msgpack.template.builder.TemplateBuilderChain;
import org.msgpack.type.Value;
import org.msgpack.type.ValueType;
import java.io.*;
import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zdj on 2016/7/8.
 */
public class Test {

    @org.junit.Test
    public void tset() throws Exception{
        MessagePack m = new MessagePack();

        Class<MessagePack> clazz = MessagePack.class;
        Field field = clazz.getDeclaredField("registry");
        field.setAccessible(true);

        TemplateRegistry registry = (TemplateRegistry)field.get(m);
        Class<TemplateRegistry> registryClass = TemplateRegistry.class;

//        Template reference1 = new TemplateReference(registry,User.class);
//        Template reference2 = new TemplateReference(registry,House.class);

//        Field field2 = registryClass.getDeclaredField("cache");
//        field2.setAccessible(true);
//        Map<Type, Template<Type>> cache = (Map)field2.get(registry);
//        cache.put(User.class,reference1);
//        cache.put(House.class,reference2);


//        Field field1 = registryClass.getDeclaredField("chain");
//        field1.setAccessible(true);
//        TemplateBuilderChain chain = (TemplateBuilderChain)field1.get(registry);
//        TemplateBuilder builder = chain.select(User.class,false);

//        Method method = registryClass.getDeclaredMethod("buildAndRegister", TemplateBuilder.class,Class.class,boolean.class, FieldList.class);
//        method.setAccessible(true);
//        method.invoke(registry,builder,User.class,false,null);

        MyJavassistTemplateBuilder builder = new MyJavassistTemplateBuilder(registry);
        builder.buildAndRegister(User.class);
        builder.buildAndRegister(House.class);

        byte[] b = m.write(new User());

        User u = m.read(b,User.class);
    }


    @org.junit.Test
    public void xbuxTest() throws Exception{
        XBusClient xBusClient = new XBusClient(new XbusConfig(new String[]{"xbus.qa.91jkys.com:4433"}, "D://clitest.ks" , "123456"));

        System.out.println(xBusClient.toString());
    }

    @org.junit.Test
    public void yamlTest() throws Exception{
        Yaml yaml = new Yaml(new BeanRepresenter());

        String s = yaml.dump(TestServiceImpl.class);

        System.out.print(s);
    }

/*    @org.junit.Test
    public void mapType(){
        Map<String, House> map = new HashMap<String, House>();
        Class c = map.getClass();

        ParameterizedType t = (ParameterizedType)c.getGenericSuperclass();

        Type[] tps = t.getActualTypeArguments();

        for(Type i : tps){
            System.out.println(i.getTypeName());
        }

    }*/
}
