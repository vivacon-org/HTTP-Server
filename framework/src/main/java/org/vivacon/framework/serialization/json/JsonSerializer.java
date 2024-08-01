package org.vivacon.framework.serialization.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivacon.framework.serialization.Serializer;

import java.lang.reflect.Field;
import java.util.Stack;

public class JsonSerializer implements Serializer {

    private static final Logger LOG = LoggerFactory.getLogger(JsonSerializer.class);

    public String serialize(Object obj) {
        JsonNode rootNode = new JsonNode();
        Stack<SerializationContext> stack = new Stack<>();
        stack.push(new SerializationContext(rootNode, obj, obj.getClass()));

        while (!stack.isEmpty()) {
            SerializationContext context = stack.pop();
            JsonNode jsonNode = context.jsonNode;
            Object currentObj = context.obj;
            Class<?> clazz = context.clazz;

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {

                field.setAccessible(true);
                String fieldName = field.getName();

                Object fieldValue;
                try {
                    fieldValue = field.get(currentObj);
                } catch (IllegalAccessException e) {
                    LOG.debug(String.format("IllegalAccessException can not get the value of field %s", field));
                    fieldValue = null;
                }

                if (fieldValue == null) {
                    // TODO: maybe in the future we could expose a config that help dev configured should we serialize
                    //  this null value as 'null' or not, but now for simplicity aspect, just ignore this field
                    continue;
                }

                if (fieldValue.getClass().isPrimitive() || fieldValue instanceof String) {
                    JsonNode childNode = new JsonNode();
                    jsonNode.addChild(fieldName, childNode);
                    childNode.setValue(fieldValue.toString());
                    continue;
                }

                JsonNode childNode = new JsonNode();
                Class<?> fieldClass = fieldValue.getClass();
                jsonNode.addChild(fieldName, childNode);
                stack.push(new SerializationContext(childNode, fieldValue, fieldClass));
            }
        }

        return rootNode.toString();
    }

    private static class SerializationContext {
        JsonNode jsonNode;
        Object obj;
        Class<?> clazz;

        SerializationContext(JsonNode jsonNode, Object obj, Class<?> clazz) {
            this.jsonNode = jsonNode;
            this.obj = obj;
            this.clazz = clazz;
        }
    }
}
