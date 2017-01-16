package com.jkys.phobos.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jkys.phobos.proto.ParamsType;

import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Created by lo on 1/15/17.
 */
public class JsonSerializer implements Serializer {
    private static Gson gson = new GsonBuilder().registerTypeAdapterFactory(new ParamsTypeAdapterFactory()).create();
    private Type targetType;

    public JsonSerializer(Type targetType) {
        this.targetType = targetType;
    }

    @Override
    public byte[] encode(Object object) {
        return gson.toJson(object).getBytes();
    }

    @Override
    public Object decode(byte[] data) {
        return gson.fromJson(new String(data), targetType);
    }

    @Override
    public Object[] decodeArray(byte[] data) {
        return gson.fromJson(new String(data), targetType);
    }

    private static class ParamsTypeAdapterFactory implements TypeAdapterFactory {
        private static class ParamsAdapter extends TypeAdapter {
            private static final Gson gson = new Gson();
            private TypeAdapter adapters[];

            ParamsAdapter(ParamsType paramsType) {
                adapters = new TypeAdapter[paramsType.getTypes().length];
                for (int i = 0; i < adapters.length; i++) {
                    adapters[i] = gson.getAdapter(TypeToken.get(paramsType.getTypes()[i]));
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public void write(JsonWriter out, Object value) throws IOException {
                Object[] args = (Object[]) value;
                out.beginArray();
                for (int i = 0; i < args.length; i++) {
                    adapters[i].write(out, args[i]);
                }
                out.endArray();
            }

            @Override
            public Object read(JsonReader in) throws IOException {
                Object[] args = new Object[adapters.length];
                in.beginArray();
                for (int i = 0; i< args.length; i++) {
                    args[i] = adapters[i].read(in);
                }
                in.endArray();
                return args;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> token) {
            Type type = token.getType();
            if (type instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) type;
                if (ptype.getRawType().equals(ParamsType.Params.class)) {
                    return new ParamsAdapter(new ParamsType(ptype.getActualTypeArguments()));
                }
            }
            return null;
        }
    }
}
