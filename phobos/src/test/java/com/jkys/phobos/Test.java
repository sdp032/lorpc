package com.jkys.phobos;

import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.client.XBusConfig;
import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.service.Ha;
import com.jkys.phobos.util.yaml.BeanRepresenter;
import com.jkys.phobos.util.yaml.PhobosRepresentr;
import com.jkys.phobos.util.yaml.Yaml;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.msgpack.MessagePack;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * Created by zdj on 2016/7/8.
 */
public class Test {

    @org.junit.Test
    public void tset() throws Exception {

        MessagePack mp = MsgpackUtil.MESSAGE_PACK;


        Method method = Ha.class.getMethods()[0];
        List<List<String>> lists = new LinkedList<>();
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        list1.add(null);
        list1.add("1");
        list2.add("2");
        list2.add(null);
        lists.add(list1);
        lists.add(list2);

        byte[] param1 = mp.write(lists);

        Map<String, List<String>> map = new HashMap();
        map.put("1", list1);

        byte[] param2 = mp.write(map);
        Value value = mp.read(param2);
        if(value.isMapValue()){
            MapValue mv = value.asMapValue();
            Value [] vss = mv.getKeyValueArray();
            System.out.println();
        }


        Class c = method.getParameterTypes()[0];
        Type t = method.getGenericParameterTypes()[0];

        System.out.println(String[].class.getName());

        Class cc = Class.forName("java.util.List");

        System.out.println(c.getTypeName());
        System.out.println(t.getTypeName());
        System.out.println(((ParameterizedType)t).getActualTypeArguments()[0].getTypeName());
    }


    @org.junit.Test
    public void xbuxTest() throws Exception {
        XBusClient xBusClient = new XBusClient(new XBusConfig(new String[]{"xbus.qa.91jkys.com:4433"}, "D://clitest.ks", "123456"));

        System.out.println(xBusClient.toString());
    }

    @org.junit.Test
    public void yamlTest() throws Exception {
        Yaml yaml = new Yaml(new PhobosRepresentr(new BeanRepresenter()));


        String s = yaml.dump(Ha.class);

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

        Integer a = 99;
        char c = (char) (int)a;

        System.out.println();
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
