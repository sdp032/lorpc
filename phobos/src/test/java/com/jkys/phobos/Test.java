package com.jkys.phobos;

import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.client.XbusConfig;
import com.jkys.phobos.codec.MyJavassistTemplateBuilder;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.service.TestService;
import com.jkys.phobos.util.SerializaionUtil;
import com.jkys.phobos.util.yaml.BeanRepresenter;
import com.jkys.phobos.util.yaml.Yaml;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.msgpack.MessagePack;
import org.msgpack.template.TemplateRegistry;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * Created by zdj on 2016/7/8.
 */
public class Test {

    @org.junit.Test
    public void tset() throws Exception {
        MessagePack m = new MessagePack();

        Class<MessagePack> clazz = MessagePack.class;
        Field field = clazz.getDeclaredField("registry");
        field.setAccessible(true);

        TemplateRegistry registry = (TemplateRegistry) field.get(m);
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

        User u = m.read(b, User.class);
    }


    @org.junit.Test
    public void xbuxTest() throws Exception {
        XBusClient xBusClient = new XBusClient(new XbusConfig(new String[]{"xbus.qa.91jkys.com:4433"}, "D://clitest.ks", "123456"));

        System.out.println(xBusClient.toString());
    }

    @org.junit.Test
    public void yamlTest() throws Exception {
        Yaml yaml = new Yaml(new BeanRepresenter());

        String s = yaml.dump(TestService.class);

        System.out.print(s);
    }

    @org.junit.Test
    public void mapType() throws Exception {
        System.out.println(System.getProperty("java.version"));
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.makeClass(new FileInputStream(new File("C:\\Users\\zdj\\Desktop\\Test.class")));


        for (CtMethod m : ctClass.getDeclaredMethods()) {

            System.out.println("方法名称：" + m.getName());

            MethodInfo methodInfo = m.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);

            if (attr != null) {
                int pos = javassist.Modifier.isStatic(m.getModifiers()) ? 0 : 1;
                for (int i = 0; i < m.getParameterTypes().length; i++) {
                    System.out.println("参数名称：" + attr.variableName(i + pos));
                }
            }
        }
    }

    @org.junit.Test
    public void address() throws Exception {
        InetAddress ip = null;
        List<String> ipList = new ArrayList<String>();
        Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
                .getNetworkInterfaces();
        while (netInterfaces.hasMoreElements()) {
            NetworkInterface ni = (NetworkInterface) netInterfaces
                    .nextElement();
            Enumeration<InetAddress> ips = ni.getInetAddresses();
            while (ips.hasMoreElements()) {
                ip = (InetAddress) ips.nextElement();
                if (!ip.isLoopbackAddress()
                        && ip.getHostAddress().matches(
                        "(\\d{1,3}\\.){3}\\d{1,3}")) {
                    ipList.add(ip.getHostAddress());
                }
            }
        }
    }

    @org.junit.Test
    public void ser() throws Exception{

        System.out.println(HashMap.class.getSuperclass());

        HashMap<String, List<Map<String,String>>> map = new HashMap<>();
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> m = new HashMap<>();
        m.put("level3", "hahaha");
        m.put("l3","xxhxh");
        list.add(m);
        map.put("1", list);

        byte[] b = SerializaionUtil.objectToBytes(map, Header.SerializationType.MAGPACK.serializationType);

        HashMap map1 = SerializaionUtil.bytesToObject(b, map.getClass(), Header.SerializationType.MAGPACK.serializationType);

        System.out.println(1);
    }

    @org.junit.Test
    public void testWait() throws Exception{
        Test test = new Test();
        List list1 = new ArrayList();
        new Thread(()->{
            try {
                test.testW("a", list1);
            }catch (Exception e){}
        }).start();
        Thread.sleep(1000);


        List list2 = new ArrayList();
        new Thread(()->{
            try {
                test.testW("b", list2);
            }catch (Exception e){}
        }).start();
        Thread.sleep(1000);

        synchronized (list2){
            list2.notify();
        }
    }


    public void testW(String a, List list) throws Exception{

        list.add(a);

        synchronized (list){
            System.out.println("syn"+a);
            list.wait(1000000000);
        }

        System.out.println(list.get(0));
    }
}
