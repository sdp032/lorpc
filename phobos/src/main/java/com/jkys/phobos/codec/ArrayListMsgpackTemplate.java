package com.jkys.phobos.codec;

import com.jkys.phobos.remote.protocol.Header;
import com.jkys.phobos.util.SerializaionUtil;
import org.msgpack.annotation.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zdj on 16-12-8.
 */
@Message
public class ArrayListMsgpackTemplate {

    /*private List<String> valueTypeNames;
    private List<byte[]> values;

    public ArrayListMsgpackTemplate(){}

    public ArrayListMsgpackTemplate(ArrayList list) throws Exception{

        if(list == null) throw new RuntimeException("list is null!");

        valueTypeNames = new ArrayList<>();
        values = new ArrayList<>();

        for(Object value : list){
            if(value == null){
                valueTypeNames.add(null);
                values.add(null);
                continue;
            }
            valueTypeNames.add(value.getClass().getName());
            values.add(SerializaionUtil.objectToBytes(value, Header.SerializationType.MAGPACK.serializationType));
        }
    }

    public ArrayList toArrayList() throws Exception{

        ArrayList list = new ArrayList();
        for(int i = 0; i < valueTypeNames.size(); i++){
            list.add(
                    valueTypeNames.get(i) == null
                            ? null
                            : SerializaionUtil.bytesToObject(values.get(i), Class.forName(valueTypeNames.get(i)), Header.SerializationType.MAGPACK.serializationType)
            );
        }
        return list;
    }

    public List<String> getValueTypeNames() {
        return valueTypeNames;
    }

    public void setValueTypeNames(List<String> valueTypeNames) {
        this.valueTypeNames = valueTypeNames;
    }

    public List<byte[]> getValues() {
        return values;
    }

    public void setValues(List<byte[]> values) {
        this.values = values;
    }*/
}
