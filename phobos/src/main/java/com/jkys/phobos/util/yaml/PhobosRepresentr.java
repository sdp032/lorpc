package com.jkys.phobos.util.yaml;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zdj on 16-12-21.
 */
public class PhobosRepresentr implements Representer<Class> {

    private BeanRepresenter beanRepresenter;

    public PhobosRepresentr(BeanRepresenter beanRepresenter){
        this.beanRepresenter = beanRepresenter;
    }

    @Override
    public String represent(Class aClass) throws Exception {

        String desc = beanRepresenter.represent(aClass);

        System.out.println(desc);
        desc = desc
                .replace("type: long\n", "type: i64\n")
                .replace("type: int\n", "type: i32\n")
                .replace("type: short\n", "type: i16\n")
                .replace("type: char\n", "type: u8\n")
                .replace("type: byte\n", "type: i8\n")
                .replace("type: double\n", "type: float64\n")
                .replace("type: float\n", "type: float32\n")
                .replace("type: boolean\n", "type: bool\n")
                .replace("type: [J\n", "type: [i64]\n")
                .replace("type: [I\n", "type: [i32]\n")
                .replace("type: [S\n", "type: [i16]\n")
                .replace("type: [C\n", "type: [u8]\n")
                .replace("type: [D\n", "type: [float64]\n")
                .replace("type: [F\n", "type: [float32]\n")
                .replace("type: [B\n", "type: bytes\n")
                .replace("type: [Z\n", "type: [bool]\n")
                .replace("java.lang.Long", "i64")
                .replace("java.lang.Integer", "i32")
                .replace("java.lang.Short" ,"i16")
                .replace("java.lang.Character", "u8")
                .replace("java.lang.Double", "float64")
                .replace("java.lang.Float", "float32")
                .replace("java.lang.Boolean", "bool")
                .replace("java.math.BigDecimal", "bigint")
                .replace("java.lang.String", "string")
                .replace("java.util.HashMap", "map")
                .replace("java.util.Map", "map")
                .replace("java.util.ArrayList", "list")
                .replace("java.util.List", "list")
                .replace("[Ljava.lang.Byte", "bytes");



        String pattern = "(\\[L.*?)[\n]";
        Pattern r = Pattern.compile(pattern);
        Matcher m;
        while (true){
            m = r.matcher(desc);
            if(m.find()){
                String newStr = m.group(0).replace("[L", "[").replace("\n", "]\n");
                desc = m.replaceFirst(newStr);
            }else {
                break;
            }
        }

        return desc;
    }
}
