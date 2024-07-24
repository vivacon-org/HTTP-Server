package org.vivacon.framework.serialization;

import java.lang.reflect.Field;
import java.util.Stack;

public class StandardJsonSerializer implements JsonSerializer {

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
                    throw new RuntimeException(e);
                }

                JsonNode childNode = new JsonNode();
                if (fieldValue == null) {
                    childNode.setValue("null");
                } else if (fieldValue.getClass().isPrimitive() || fieldValue instanceof String) {
                    childNode.setValue(fieldValue.toString());
                } else {
                    Class<?> fieldClass = fieldValue.getClass();
                    jsonNode.addChild(fieldName, childNode);
                    stack.push(new SerializationContext(childNode, fieldValue, fieldClass));
                }
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
