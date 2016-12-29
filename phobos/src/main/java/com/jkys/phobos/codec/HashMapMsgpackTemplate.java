package com.jkys.phobos.codec;

import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.util.SerializaionUtil;
import org.msgpack.annotation.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zdj on 16-12-8.
 */
@Message
public class HashMapMsgpackTemplate {

    /*private List<String> keyTypeNames;
    private List<byte[]> keys;
    private List<String> valueTypeNames;
    private List<byte[]> valus;

    public HashMapMsgpackTemplate(){}

    public HashMapMsgpackTemplate(HashMap map) throws Exception{

        keyTypeNames = new ArrayList<>();
        keys = new ArrayList<>();
        valueTypeNames = new ArrayList<>();
        valus = new ArrayList<>();
        if (map == null) throw new RuntimeException("map is nill!");
        for(Object key : map.keySet()){
            keyTypeNames.add(key == null ? null : key.getClass().getName());
            keys.add(key == null ? null : SerializaionUtil.objectToBytes(key, Header.SerializationType.MAGPACK.serializationType));
            valueTypeNames.add(map.get(key) == null ? null : map.get(key).getClass().getName());
            valus.add(map.get(key) == null ? null : SerializaionUtil.objectToBytes(map.get(key), Header.SerializationType.MAGPACK.serializationType));

        }
    }

    public HashMap toHashMap() throws Exception{
        HashMap hashMap = new HashMap();

        for(int i = 0; i < keys.size(); i++){
            Object key = null;
            Object value  = null;
            if(keyTypeNames != null){
                key = SerializaionUtil.bytesToObject(keys.get(i), Class.forName(keyTypeNames.get(i)), Header.SerializationType.MAGPACK.serializationType);
            }
            if(valueTypeNames.get(i) != null){
                value = SerializaionUtil.bytesToObject(valus.get(i), Class.forName(valueTypeNames.get(i)), Header.SerializationType.MAGPACK.serializationType);
            }
            hashMap.put(key,value);
        }
        return hashMap;
    }

    public List<String> getKeyTypeNames() {
        return keyTypeNames;
    }

    public void setKeyTypeNames(List<String> keyTypeNames) {
        this.keyTypeNames = keyTypeNames;
    }

    public List<byte[]> getKeys() {
        return keys;
    }

    public void setKeys(List<byte[]> keys) {
        this.keys = keys;
    }

    public List<String> getValueTypeNames() {
        return valueTypeNames;
    }

    public void setValueTypeNames(List<String> valueTypeNames) {
        this.valueTypeNames = valueTypeNames;
    }

    public List<byte[]> getValus() {
        return valus;
    }

    public void setValus(List<byte[]> valus) {
        this.valus = valus;
    }*/
}
