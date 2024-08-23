package org.vivacon.framework.serialization.json.serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivacon.framework.serialization.common.StrGenerator;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class StdJsonSerializer implements JsonSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(StdJsonSerializer.class);

    @Override
    public StrGenerator serialize(Object obj, JsonGenerator gen, JsonSerializationContext context) {
        if (obj == null) {
            gen.writeNull();
            return gen;
        }

        if (obj instanceof Collection) {
            return serializeCollection((Collection<?>) obj, gen, context);
        }

        if (obj.getClass().isArray()) {
            return serializeArray(obj, gen, context);
        }

        if (obj instanceof Map) {
            return serializeMap((Map<?, ?>) obj, gen, context);
        }

        if (obj.getClass().isPrimitive() || obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            return serializePrimitive(obj, gen, context);
        }

        return serializeObjectFields(obj, gen, context);
    }

    private JsonGenerator serializePrimitive(Object obj, JsonGenerator gen, JsonSerializationContext context) {
        if (obj instanceof String) {
            gen.writeString((String) obj);
            return gen;
        }

        if (obj instanceof Number) {
            gen.writeNumber((Number) obj);
            return gen;
        }

        if (obj instanceof Boolean) {
            gen.writeString(obj.toString());
        }

        return gen;
    }

    private JsonGenerator serializeCollection(Collection<?> collection, JsonGenerator gen, JsonSerializationContext context) {
        gen.writeStartArray();
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                gen.writeSeparator();
            }
            serialize(item, gen, context);
            first = false;
        }
        gen.writeEndArray();
        return gen;
    }

    private JsonGenerator serializeArray(Object array, JsonGenerator gen, JsonSerializationContext context) {
        gen.writeStartArray();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                gen.writeSeparator();
            }
            serialize(Array.get(array, i), gen, context);
        }
        gen.writeEndArray();
        return gen;
    }

    private JsonGenerator serializeMap(Map<?, ?> map, JsonGenerator gen, JsonSerializationContext context) {
        gen.writeStartObject();
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                gen.writeSeparator();
            }
            gen.writeFieldName(entry.getKey().toString());
            serialize(entry.getValue(), gen, context);
            first = false;
        }
        gen.writeEndObject();
        return gen;
    }

    private JsonGenerator serializeObjectFields(Object obj, JsonGenerator gen, JsonSerializationContext context) {
        gen.writeStartObject();
        gen.writeNextLine();
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

            gen.writeFieldName(fieldName);
            serialize(fieldValue, gen, context);

            runner++;
            if (runner < fields.length) {
                gen.writeSeparator();
            }
            gen.writeNextLine();
        }

        gen.writeEndObject();
        return gen;
    }
}