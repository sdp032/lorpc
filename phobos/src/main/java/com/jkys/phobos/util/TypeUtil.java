package com.jkys.phobos.util;

import com.jkys.phobos.annotation.PhobosGroup;
import com.jkys.phobos.annotation.PhobosVersion;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Set;

/**
 * Created by zdj on 2016/7/13.
 */
public class TypeUtil {

    public static void getAllSerializeType(Set set,Class clz){

        if(isBaseDataType(clz)||clz == Unsafe.class||clz.getPackage().getName().indexOf("java.util")!=-1){
            return;
        }
        set.add(clz);
        Field[] fields = clz.getDeclaredFields();
        for (Field f : fields){
            f.setAccessible(true);
            getAllSerializeType(set,f.getType());
        }
    }

    public static boolean isBaseDataType(Class clz) {
        return clz == Integer.class||
                clz == Double.class||
                clz == Long.class||
                clz == Byte.class||
                clz == Float.class||
                clz == Character.class||
                clz == Short.class||
                clz == Boolean.class|| clz == String.class||
                clz == BigDecimal.class||
                clz == BigInteger.class ||
                clz == Date.class ||
                clz.isPrimitive();
    }
}
