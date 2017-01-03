package com.jkys.phobos.codec;

import com.jkys.phobos.constant.ErrorEnum;
import com.jkys.phobos.exception.PhobosException;
import com.jkys.phobos.util.GenericTypeUtil;
import com.jkys.phobos.util.TypeUtil;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
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
        Object oo = objectToMap(o);
        return MsgpackUtil.MESSAGE_PACK.write(this.objectToMap(o));
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
            }else if(clazz == Date.class){
                result = new Date(value.asIntegerValue().getLong());
            } else {
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
            Value[] vs = value.asArrayValue().getElementArray();
            if(clazz == new char[0].getClass()){
                char[] chars = (char[]) Array.newInstance(char.class, vs.length);
                for(int i = 0; i < chars.length; i ++){
                    chars[i] = (char) vs[i].asIntegerValue().getInt();
                }
                return chars;
            }

            if(clazz.isArray()){
                try {
                    Class eClass = Class.forName(GenericTypeUtil.arrayType(clazz.getTypeName()));
                    Object array = Array.newInstance(eClass, vs.length);
                    for(int i = 0; i < vs.length; i ++){
                        ((Object[])array)[i] = msgValueToObject(vs[i], eClass, null);
                    }
                    result = array;
                }catch (ClassNotFoundException e){
                    result = MsgpackUtil.MESSAGE_PACK.convert(value, clazz);
                }

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
                for(Value v : vs){
                    /*if(paramType.isPrimitive() || List.class.isAssignableFrom(paramType) || Map.class.isAssignableFrom(paramType)){
                        list.add(msgValueToObject(v, paramType, nesting));
                    } else {
                        list.add(MsgpackUtil.MESSAGE_PACK.convert(v, paramType));
                    }*/
                    list.add(msgValueToObject(v, paramType, nesting));
                }
                result = list;
            }else {
                result = MsgpackUtil.MESSAGE_PACK.convert(value, clazz);
            }
        }else if(value.isMapValue()){
            MapValue mv = value.asMapValue();
            Iterator<Value> keys = mv.keySet().iterator();
            if(!Map.class.isAssignableFrom(clazz)) {
                result = clazz.newInstance();
                while (keys.hasNext()){
                    Value vKey = keys.next();
                    Value vValue = mv.get(vKey);
                    String filedName = (String) msgValueToObject(vKey, String.class, null);
                    Field field = clazz.getDeclaredField(filedName);
                    field.setAccessible(true);
                    String [] _genericType = null;
                    if(field.getGenericType() instanceof ParameterizedType){
                        Type[] types = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
                        _genericType = new String[types.length];
                        for(int i = 0; i < types.length; i++){
                            _genericType[i] = types[i].getTypeName();
                        }
                    }
                    Object filedVal = msgValueToObject(vValue, field.getType(), _genericType);
                    field.set(result,field.getType() == char.class || field.getType() == Character.class ? (char)(int) filedVal : filedVal);
                }
                return result;
            }
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

            Map<Object, Object> map = new HashMap();
            while (keys.hasNext()){
                Value vKey = keys.next();
                Value vValue = mv.get(vKey);
                Object key = msgValueToObject(vKey, keyType, keyNesting);
                Object val = msgValueToObject(vValue, valType, valNesting);
                map.put(key, val);
            }
            result = map;
        }else {
            try {
                result = MsgpackUtil.MESSAGE_PACK.convert(value, clazz);
            }catch (Exception e){
                throw new PhobosException(ErrorEnum.INVALID_PARAMS.name(), "转换失败", e);
            }

        }

        return result;
    }

    private Object objectToMap(Object o) throws Exception{
        if(null == o){
            return null;
        }
        if(TypeUtil.isBaseDataType(o.getClass())){
            return o;
        }else if(o.getClass().isArray()){
            Object[] objects = null;
            try {
                objects = (Object[]) o;
            }catch (ClassCastException e){
                if(o.getClass().equals(new char[0].getClass())){
                    objects = new Object[((char[])o).length];
                    for(int i=0; i < objects.length; i++){
                        objects[i] = ((char[])o)[i];
                    }
                }else {
                    return o;
                }
            }
            List<Object> list = new ArrayList<>();
            for(int i = 0; i < objects.length; i ++){
                list.add(objectToMap(objects[i]));
            }
            return list;
        }else if(List.class.isAssignableFrom(o.getClass())){
            List<Object> list = (List)o;
            List<Object> result = new ArrayList<>();
            for(int i = 0; i < list.size(); i ++){
                result.add(objectToMap(list.get(i)));
            }
            return result;
        }else if(Map.class.isAssignableFrom(o.getClass())){
            Map<Object, Object> map = (Map)o;
            Map<Object, Object> result = new HashMap<>();
            Iterator<Object> keys = map.keySet().iterator();
            while (keys.hasNext()){
                Object oldKey = keys.next();
                Object key = objectToMap(oldKey);
                Object val = objectToMap(map.get(oldKey));
                result.put(key, val);
            }
            return result;
        }else {
            Map<String, Object> map = new HashMap<>();
            Field[] fields = o.getClass().getDeclaredFields();
            for(Field field : fields){
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldVal = objectToMap(field.get(o));
                map.put(fieldName, fieldVal);
            }
            return map;
        }
    }
}
