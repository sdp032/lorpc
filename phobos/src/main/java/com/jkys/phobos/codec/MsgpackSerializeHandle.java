package com.jkys.phobos.codec;

import com.jkys.phobos.constant.ErrorEnum;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.util.GenericTypeUtil;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by zdj on 16-12-26.
 */
public class MsgpackSerializeHandle implements SerializeHandle {

    private static MsgpackSerializeHandle handle = null;

    private MsgpackSerializeHandle(){}

    public static synchronized MsgpackSerializeHandle getInstance (){
        if(handle == null){
            handle = new MsgpackSerializeHandle();
        }

        return handle;
    }

    @Override
    public byte[] objectToBytes(Object o) throws Exception{
        return MsgpackUtil.MESSAGE_PACK.write(o);
    }

    @Override
    public Object[] bytesToParams(byte[] bytes, Class[] paramsType, Type[] genericParamsType) throws Exception{

        Value value = MsgpackUtil.MESSAGE_PACK.read(bytes);
        if(value.isNilValue()){
            return null;
        }

        Value[] vs = value.asArrayValue().getElementArray();
        if(vs.length != paramsType.length){
            throw new PhobosException(ErrorEnum.INVALID_PARAMS.name(),"参数个数不匹配！");
        }

        Object[] params = new Object[vs.length];

        for(int i = 0; i < vs.length; i++){
            String[] genericType = null;
            if(genericParamsType[i] instanceof ParameterizedType){
                Type[] ts = ((ParameterizedType)genericParamsType[i]).getActualTypeArguments();
                genericType = new String[ts.length];
                for(int j = 0; j < ts.length; j++){
                    genericType[j] = ts[j].getTypeName().replaceAll(" ", "");
                }
            }
            params[i] = msgValueToObject(vs[i], paramsType[i], genericType);
        }

        return params;
    }

    @Override
    public Object bytesToReturnVal(byte[] bytes,Class returnType, Type genericReturnType) throws Exception {
        Value value = MsgpackUtil.MESSAGE_PACK.read(bytes);
        String[] genericTypes = null;
        if(genericReturnType instanceof ParameterizedType){
            Type[] ts = ((ParameterizedType)genericReturnType).getActualTypeArguments();
            genericTypes = new String[ts.length];
            for(int i = 0; i < ts.length; i ++){
                genericTypes[i] = ts[i].getTypeName().replaceAll(" ", "");
            }
        }
        return msgValueToObject(value, returnType, genericTypes);
    }


    private Object msgValueToObject(Value value, Class clazz, String[] genericType) throws  Exception{

        Object result = null;

        if(value.isNilValue()){
            if(clazz.isPrimitive())
                throw new PhobosException(ErrorEnum.INVALID_PARAMS.name(), "参数不能为空！");
            result = null;
        }else if(value.isBooleanValue()){
            if(clazz != boolean.class && clazz != Boolean.class)
                throw new PhobosException(ErrorEnum.INVALID_PARAMS.name(), "参数类型不匹配！");
            result = value.asBooleanValue().getBoolean();
        } else if(value.isIntegerValue()){
            if(clazz == byte.class || clazz == Byte.class){
                result = value.asIntegerValue().getByte();
            }else if(clazz == char.class || clazz == Character.class){
                result = value.asIntegerValue().getInt();
            }else if(clazz == short.class || clazz == Short.class){
                result = value.asIntegerValue().getShort();
            }else if(clazz == int.class || clazz == Integer.class){
                result = value.asIntegerValue().getInt();
            }else if(clazz == long.class || clazz == Long.class){
                result = value.asIntegerValue().getLong();
            }else if(clazz == BigInteger.class){
                result = value.asIntegerValue().getBigInteger();
            }else {
                throw new PhobosException(ErrorEnum.INVALID_PARAMS.name(), "参数类型不匹配！");
            }
        }else if(value.isFloatValue()){
            if(clazz == double.class || clazz == Double.class){
                result = value.asFloatValue().getDouble();
            }else if (clazz == float.class || clazz == Float.class){
                result = value.asFloatValue().getFloat();
            }else {
                throw new PhobosException(ErrorEnum.INVALID_PARAMS.name(), "参数类型不匹配！");
            }
        }else if(value.isArrayValue()){
            if(clazz.isArray()){
                result = MsgpackUtil.MESSAGE_PACK.convert(value, clazz);
            }else if(List.class.isAssignableFrom(clazz)){
                if(genericType == null || genericType.length != 1)
                    throw new PhobosException(ErrorEnum.INVALID_PARAMS.name(), "List类型参数需要确切的泛型信息！");
                Map<String, Object> typeMap = GenericTypeUtil.resolve(genericType[0]);
                String type = (String)typeMap.get("type");
                Class paramType = null;
                if(GenericTypeUtil.isArray(type)){
                    paramType = Array.newInstance(Class.forName(GenericTypeUtil.arrayType(type)),0).getClass();
                }else {
                    paramType = Class.forName((String)typeMap.get("type"));
                }
                String[] nesting = (String[]) typeMap.get("nesting");
                List<Object> list = new ArrayList();
                Value[] vs = value.asArrayValue().getElementArray();
                for(Value v : vs){
                    if(paramType.isPrimitive() || List.class.isAssignableFrom(paramType) || Map.class.isAssignableFrom(paramType)){
                        list.add(msgValueToObject(v, paramType, nesting));
                    } else {
                        list.add(MsgpackUtil.MESSAGE_PACK.convert(v, paramType));
                    }
                }
                result = list;
            }
        }else if(value.isMapValue()){
            if(!Map.class.isAssignableFrom(clazz)) throw new PhobosException(ErrorEnum.INVALID_PARAMS.name(), "参数类型不匹配！");
            if(genericType.length != 2) throw new PhobosException(ErrorEnum.INVALID_PARAMS.name(), "Map类型参数需要确切的泛型信息！");

            Map<String, Object> keyMap = GenericTypeUtil.resolve(genericType[0]);
            Map<String, Object> valMap = GenericTypeUtil.resolve(genericType[1]);

            String keyTypeStr = (String) keyMap.get("type");
            String valTypeStr = (String) valMap.get("type");

            Class keyType = null;
            Class valType = null;

            if(GenericTypeUtil.isArray(keyTypeStr)){
                keyType = Array.newInstance(Class.forName(GenericTypeUtil.arrayType(keyTypeStr)), 0).getClass();
            }else {
                keyType = Class.forName(keyTypeStr);
            }

            if(GenericTypeUtil.isArray(valTypeStr)){
                valType = Array.newInstance(Class.forName(GenericTypeUtil.arrayType(valTypeStr)), 0).getClass();
            }else {
                valType = Class.forName(valTypeStr);
            }

            String[] keyNesting = (String[]) keyMap.get("nesting");
            String[] valNesting = (String[]) valMap.get("nesting");

            MapValue mv = value.asMapValue();
            Iterator<Value> keys = mv.keySet().iterator();
            Map<Object, Object> map = new HashMap();
            while (keys.hasNext()){
                Value vKey = keys.next();
                Value vValue = mv.get(vKey);
                boolean b = vKey.isRawValue();
                Object key = msgValueToObject(vKey, keyType, keyNesting);
                Object val = msgValueToObject(vValue, valType, valNesting);
                map.put(key, val);
            }
            result = map;
        }else {
            result = MsgpackUtil.MESSAGE_PACK.convert(value, clazz);
        }

        return result;
    }
}
