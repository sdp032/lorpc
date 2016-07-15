package com.jkys.phobos;

import com.jkys.phobos.codec.MsgpackUtil;
import com.jkys.phobos.netty.AbstractServerChannelHandler;
import com.jkys.phobos.netty.DefaultServerChannelHandler;
import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.remote.protocol.PhobosRequest;
import com.jkys.phobos.remote.protocol.Request;
import com.jkys.phobos.util.TypeUtil;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import org.msgpack.MessagePack;
import org.msgpack.template.Templates;
import org.msgpack.type.Value;
import org.msgpack.type.ValueType;

import java.io.*;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zdj on 2016/7/8.
 */
public class Test {

    @org.junit.Test
    public void tset() throws Exception{

        BufferedReader bs = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:\\Users\\zdj\\Desktop\\zmxy_user.txt")),"GB2312"));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("C:\\Users\\zdj\\Desktop\\zmxy_user.sql")),"GB2312"));
        List<String> user =  new ArrayList<String>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String line = null;
        int userId = 0;
        int orderId = 0;
        while ((line = bs.readLine()) != null){
            if(userId == 0){
                userId ++ ;
                continue;
            }
            line = line.replaceAll("\"","");
            line = userId + "," + line;
            user.add(line);
            String create_time = format.format(new Date(Long.valueOf(line.split(",")[7])*1000));
            String update_time = format.format(new Date(Long.valueOf(line.split(",")[8])*1000));
            String sql = "insert into zm_user (id,zmxy_id,name,cert_no,score,create_time,update_time) values ("
                    + userId ++ + ","
                    + "'" + line.split(",")[2] + "',"
                    + "'" + line.split(",")[3] + "',"
                    + "'" + line.split(",")[4] + "',"
                    + line.split(",")[6] + ","
                    + "'" + create_time  +"',"
                    + "'" + update_time  + "');\r\n";
            bw.write(sql);
            bw.flush();
        }

        bs = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:\\Users\\zdj\\Desktop\\zmxy_order.txt")),"GB2312"));
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("C:\\Users\\zdj\\Desktop\\zmxy_order.sql")),"GB2312"));
        while ((line = bs.readLine()) != null){
            if(orderId == 0){
                orderId++;
                continue;
            }
            line = line.replaceAll("\"","");
            String uid = "";
            for(String s : user){
                if (line.split(",")[1].equals(s.split(",")[1])){
                    uid = s.split(",")[0];
                    break;
                }
            }
            String status = "";
            if(line.split(",")[14].equals("back")){
                status = "PROBATION_BACK";
            }else if (line.split(",")[14].equals("sucess-shiped")){
                status = "DELIVERED";
            }else if(line.split(",")[14].equals("given")){
                status = "COMPLETE";
            }else if(line.split(",")[14].equals("sold")){
                status = "PROBATION_TO_BUY";
            }
            String create_time ="";
            String[] ss = line.split(",");
            try{
                create_time = format.format(new Date(Long.valueOf(line.split(",")[15])*1000));
            }catch (Exception e){
                e.printStackTrace();
            }

            String updataTime = format.format(new Date(Long.valueOf(line.split(",")[16])*1000));
            String t = format.format(new Date(Long.valueOf(line.split(",")[15])*1000+5184000l*1000));
            String type = line.split(",")[3].equals("rent") ? "LEASE" : "BUY";
            String sql = "insert into zm_order (id,user_id,commodity_id,commodity_name,name,phone,address,postcode,express,buy_num,real_pay,status,begin_time,expire_time,operator,order_type,create_time,update_time) values("
                    + orderId++ +","
                    + uid + ","
                    + line.split(",")[2] + ","
                    + "'" + "一期商品名称" + "',"
                    + "'" + line.split(",")[5] + "',"
                    + "'" + line.split(",")[6] + "',"
                    + "'" + line.split(",")[7] + line.split(",")[8] +"',"
                    + "'" + line.split(",")[9] +"',"
                    + "'" + line.split(",")[10] +"',"
                    + "'" + line.split(",")[11] +"',"
                    + "'" + line.split(",")[12] +"',"
                    + "'" + status +"',"
                    + "'" + create_time  +"',"
                    + "'" + t  +"',"
                    + "'" + "系统导入" +"',"
                    + "'" + type + "',"
                    + "'" + create_time  +"',"
                    + "'" + updataTime  +"');\r\n";

            bw.write(sql);
            bw.flush();
        }
    }

    @org.junit.Test
    public void test () throws Exception{

        MessagePack pack = new MessagePack();
        pack.register(Header.class);
        pack.register(Request.class);
        pack.register(PhobosRequest.class);

        List<byte[]> list = new ArrayList();

        list.add(pack.write(new Header()));
        list.add(pack.write(new Request()));
        list.add(pack.write(new PhobosRequest(new Header(),new Request())));

        byte[] b = pack.write(list);

        List<byte[]> l = pack.read(b,Templates.tList(Templates.TByteArray));

        Header h = pack.read(l.get(0),Header.class);
        Request r = pack.read(l.get(1),Request.class);
        PhobosRequest p = pack.read(l.get(2),PhobosRequest.class);
        System.out.println(11);

        Request request = new Request();
        List<byte[]> ll = new ArrayList();
        ll.add(pack.write(new Header()));
        ll.add(pack.write(new Request()));
        ll.add(pack.write(new PhobosRequest(new Header(),new Request())));
        request.setObject(ll);


        byte[] bb = pack.write(request);

        Request request1 = pack.read(bb,Request.class);
        PhobosRequest phobosRequest = pack.read(request1.getObject().get(2),PhobosRequest.class);

        System.out.println(1);

    }

    @org.junit.Test
    public void test2(){

        Set<Class> set = new HashSet();

        TypeUtil.getAllSerializeType(set,PhobosRequest.class);

        MsgpackUtil.register(set);

        System.out.println(set.size());
    }

    @org.junit.Test
    public void test3(){

        Header header = new Header();
        Header header1 = new Header();

        System.out.println(header.getSequenceId());
        System.out.println(header1.getSequenceId());
    }
}
