package com.jkys.phobos.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by zdj on 16-12-28.
 */
public class GenericTypeUtil {

    public static Map<String, Object> resolve(String genericTypeStr){

        if(StringUtils.isEmpty(genericTypeStr)) throw new NullPointerException("genericTypeStr is null");

        Map<String, Object> map = new HashMap<>();

        genericTypeStr = genericTypeStr.replaceAll(" ", "");

        String[] type = genericTypeStr.split("<", 2);
        map.put("type", type[0]);
        if(type.length == 1){ //非list和map的普通类型
            return map;
        }
        type[1] = type[1].substring(0, type[1].length() - 1);

        char[] chars = type[1].toCharArray();
        char[] newChars = new char[chars.length];
        Stack<Character> stack = new Stack<>();
        for(int i = 0; i < chars.length; i++){
            newChars[i] = chars[i];
            if(chars[i] == '<'){
                stack.push(chars[i]);
            }else if(chars[i] == '>'){
                stack.pop();
            }else if (chars[i] == ',' && stack.empty()){ //泛型有多个嵌套泛型
                newChars[i] = '|';
            }
        }

        String[] nesting = new String(newChars).split("\\|");
        map.put("nesting", nesting);

        return map;
    }

    public static boolean isArray(String type){
        if(type == null || type.length() < 2) return false;
        String postfix = type.substring(type.length() - 2, type.length());
        if("[]".equals(postfix)){
            return true;
        }
        return false;
    }

    public static String arrayType(String type){
        if(type == null || type.length() < 2) return null;
        String postfix = type.substring(type.length() - 2, type.length());
        if("[]".equals(postfix)){
            return type.substring(0, type.length() - 2);
        }
        return null;
    }
}
