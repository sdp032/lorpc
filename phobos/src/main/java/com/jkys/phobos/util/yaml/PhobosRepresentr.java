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
                .replace("type: long", "type: i64")
                .replace("type: int", "type: i32")
                .replace("type: short", "type: i16")
                .replace("type: char", "type: u8")
                .replace("type: byte", "type: i8")
                .replace("type: double", "type: float64")
                .replace("type: float", "type: float32")
                .replace("type: boolean", "type: bool")
                .replace("type: [J", "type: [i64]")
                .replace("type: [I", "type: [i32]")
                .replace("type: [S", "type: [i16]")
                .replace("type: [C", "type: [u8]")
                .replace("type: [D", "type: [float64]")
                .replace("type: [F", "type: [float32]")
                .replace("type: [B", "type: bytes")
                .replace("type: [Z", "type: [bool]")
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
