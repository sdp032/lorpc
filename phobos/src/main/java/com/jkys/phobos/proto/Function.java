package com.jkys.phobos.proto;

import com.jkys.phobos.proto.types.ProtoType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lo on 1/10/17.
 */
public class Function {
    private String name;
    private ProtoType returnType;
    private ProtoType[] params;

    public Function(String name, ProtoType returnType, ProtoType[] params) {
        this.name = name;
        this.returnType = returnType;
        this.params = params;
    }

    public Map<String, Object> dump() {
        List<Map<String, Object>> paramDescs = new ArrayList<>(params.length);
        for (ProtoType type : params) {
            paramDescs.add(type.dump());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("params", paramDescs);
        result.put("return", returnType.dump());
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProtoType getReturnType() {
        return returnType;
    }

    public void setReturnType(ProtoType returnType) {
        this.returnType = returnType;
    }

    public ProtoType[] getParams() {
        return params;
    }

    public void setParams(ProtoType[] params) {
        this.params = params;
    }
}
