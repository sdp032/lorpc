package com.jkys.phobos;

import com.github.infrmods.xbus.client.XBusClient;
import com.github.infrmods.xbus.client.XBusConfig;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * Created by zdj on 2016/7/8.
 */
public class Test {
    @org.junit.Test
    public void xbuxTest() throws Exception {
        XBusClient xBusClient = new XBusClient(new XBusConfig(new String[]{"xbus.qa.91jkys.com:4433"}, "D://clitest.ks", "123456"));

        System.out.println(xBusClient.toString());
    }

    @org.junit.Test
    public void mapType() throws Exception {
        System.out.println(System.getProperty("java.value"));
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
