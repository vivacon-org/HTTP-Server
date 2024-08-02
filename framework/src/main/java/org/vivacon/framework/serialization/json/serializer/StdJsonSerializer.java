package org.vivacon.framework.serialization.json.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class StdJsonSerializer implements JsonSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(StdJsonSerializer.class);

    @Override
    public void serialize(Object obj, JsonGenerator gen) {
        if (obj == null) {
            gen.writeNull();
            return;
        }

        Class<?> clazz = obj.getClass();
        if (clazz.isPrimitive() || obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            serializePrimitive(obj, gen);
            return;
        }

        serializeObject(obj, gen);
    }

    private void serializePrimitive(Object obj, JsonGenerator gen) {
        if (obj instanceof String) {
            gen.writeString((String) obj);
            return;
        }

        if (obj instanceof Number) {
            gen.writeNumber((Number) obj);
            return;
        }

        if (obj instanceof Boolean) {
            gen.writeString(obj.toString());
        }
    }

    private void serializeObject(Object obj, JsonGenerator gen) {
        gen.writeStartObject();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                fieldValue = null;
            }

            gen.writeFieldName(fieldName);
            serialize(fieldValue, gen);
        }
        gen.writeEndObject();
    }
}