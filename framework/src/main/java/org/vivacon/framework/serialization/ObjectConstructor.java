package org.vivacon.framework.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ObjectConstructor {

    public Object constructObject(JsonNode jsonNode, Class<?> expectedClazz) {
        Class<?> definedClazz;
        try {
            definedClazz = Class.forName(jsonNode.getValue());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("");
        }

        if (definedClazz.isAssignableFrom(expectedClazz)) {
            throw new RuntimeException("");
        }

        Object instance;
        try {
            instance = definedClazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        populateFields(instance, jsonNode);
        return instance;
    }

    private void populateFields(Object obj, JsonNode jsonNode) {
        Class<?> clazz = obj.getClass();
        for (Map.Entry<String, JsonNode> entry : jsonNode.getChildren().entrySet()) {
            String fieldName = entry.getKey();
            JsonNode childNode = entry.getValue();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);

            if (field.getType() == int.class) {
                field.setInt(obj, Integer.parseInt(childNode.getValue()));
            } else if (field.getType() == String.class) {
                field.set(obj, childNode.getValue());
            } else {
                Object nestedObject = constructObject(childNode, field.getType().getName());
                field.set(obj, nestedObject);
            }
        }
    }
}