package org.vivacon.framework.serialization.json.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivacon.framework.serialization.Serializer;
import org.vivacon.framework.serialization.StrGenerator;

import java.lang.reflect.Field;

public class StdJsonSerializer implements Serializer {
    private static final Logger LOG = LoggerFactory.getLogger(StdJsonSerializer.class);

    @Override
    public String serialize(Object obj, StrGenerator gen) {
        if(!(gen instanceof JsonGenerator)){
            throw new RuntimeException("Invalid generator, expect JsonGenerator");
        }

        JsonGenerator jsonGen = (JsonGenerator) gen;
        if (obj == null) {
            gen.writeNull();
            return jsonGen.generateString();
        }

        Class<?> clazz = obj.getClass();
        if (clazz.isPrimitive() || obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            jsonGen = serializePrimitive(obj, jsonGen);
            return jsonGen.generateString();
        }

        return serializeObject(obj, jsonGen).generateString();
    }

    private JsonGenerator serializePrimitive(Object obj, JsonGenerator gen) {
        JsonGenerator jsonGen = gen.clone();

        if (obj instanceof String) {
            jsonGen.writeString((String) obj);
            return jsonGen;
        }

        if (obj instanceof Number) {
            jsonGen.writeNumber((Number) obj);
            return jsonGen;
        }

        if (obj instanceof Boolean) {
            jsonGen.writeString(obj.toString());
        }

        return jsonGen;
    }

    private JsonGenerator serializeObject(Object obj, JsonGenerator gen) {
        JsonGenerator jsonGen = gen.clone();

        jsonGen.writeStartObject();
        jsonGen.writeNextLine();
        Field[] fields = obj.getClass().getDeclaredFields();

        int runner = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                fieldValue = null;
            }
            jsonGen.writeFieldName(fieldName);
            String fieldValueJson = serialize(fieldValue, jsonGen);
            jsonGen = new JsonGenerator(fieldValueJson);

            runner++;
            if (runner < fields.length){
                jsonGen.writeSeparator();
            }
            jsonGen.writeNextLine();
        }

        jsonGen.writeEndObject();
        return jsonGen;
    }
}