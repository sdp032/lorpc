package com.jkys.phobos.codec;

import org.msgpack.MessageTypeException;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateReference;
import org.msgpack.template.TemplateRegistry;
import org.msgpack.template.builder.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by zdj on 2016/7/19.
 *
 * 实现不使用Message注解解决循序依赖不能注册问题
 */
public class MyJavassistTemplateBuilder extends JavassistTemplateBuilder {

    private Map<Type, Template<Type>> cache;

    public MyJavassistTemplateBuilder(TemplateRegistry registry){
        super(registry);
        Field field = null;
        try {
            field = TemplateRegistry.class.getDeclaredField("cache");
            field.setAccessible(true);
            cache = (Map)field.get(registry);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Template buildAndRegister(Class<?> targetClass){

        Template newTmpl = null;
        Template oldTmpl = null;

        try {
            if (cache.containsKey(targetClass)) {
                oldTmpl = cache.get(targetClass);
            }
            newTmpl = new TemplateReference(registry, targetClass);
            cache.put(targetClass, newTmpl);
            newTmpl = this.buildTemplate(targetClass);
            return newTmpl;
        }catch (Exception e){
            if (oldTmpl != null) {
                cache.put(targetClass, oldTmpl);
            } else {
                cache.remove(targetClass);
            }
            newTmpl = null;
            if (e instanceof MessageTypeException) {
                throw (MessageTypeException) e;
            } else {
                throw new MessageTypeException(e);
            }
        }finally {
            if (newTmpl != null) {
                cache.put(targetClass, newTmpl);
            }
        }
    }

    @Override
    public <T> Template<T> buildTemplate(Class<T> targetClass, FieldEntry[] entries){

        Template<?>[] tmpls = toTemplate(entries);

        DefaultBuildContext bc = (DefaultBuildContext)createBuildContext();
        return bc.buildTemplate(targetClass, entries, tmpls);
    }

    private Template<?>[] toTemplate(FieldEntry[] from) {
        Template<?>[] tmpls = new Template<?>[from.length];
        for (int i = 0; i < from.length; ++i) {
            FieldEntry e = from[i];
            if (!e.isAvailable()) {
                tmpls[i] = null;
            } else {
                Template<?> tmpl = null;
                try{
                    tmpl = registry.lookup(e.getGenericType());
                } catch (MessageTypeException ex){
                    tmpl = buildAndRegister(e.getType());
                }
                tmpls[i] = tmpl;
            }
        }
        return tmpls;
    }
}
