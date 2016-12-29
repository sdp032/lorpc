package com.jkys.phobos.codec;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by zdj on 16-12-26.
 */
public interface SerializeHandle {

     byte[] objectToBytes(Object o) throws Exception;

     /**
      *
      * @param bytes
      * @param method
      * @return
      * @throws Exception
      *
      * 将客户端传过来的参数字节流转成参数对象
      */
     Object[] bytesToParams(byte[] bytes, Class[] paramsType, Type[] genericParamsType) throws Exception;

     /**
      *
      * @param bytes
      * @param genericReturnType
      * @return
      * @throws Exception
      *
      * 将服务端传回客户端的返回值字节流转换成返回值对象
      */
     Object bytesToReturnVal(byte[] bytes,Class returnType, Type genericReturnType) throws Exception;
}
