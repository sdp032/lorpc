package com.jkys.phobos;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zdj on 2016/7/8.
 */
public class Test {

    @org.junit.Test
    public void tset() throws Exception{

        BufferedReader bs = new BufferedReader(new FileReader(new File("C:\\Users\\zdj\\Desktop\\zmxy_user.txt")));
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("C:\\Users\\zdj\\Desktop\\nzmxy_user.txt")));
        List<String> outData =  new ArrayList<String>();
        String line = null;
        while ((line = bs.readLine()) != null){
            System.out.println(new String(line.getBytes()));
            bw.write(line);
        }

    }
}
