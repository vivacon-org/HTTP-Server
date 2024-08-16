package org.vivacon.framework.serialization.json.deserializer;

import org.vivacon.framework.serialization.json.deserializer.node.JsonArrayNode;
import org.vivacon.framework.serialization.json.deserializer.node.JsonNode;
import org.vivacon.framework.serialization.json.deserializer.node.JsonObjectNode;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StdJsonDeserializer {

    public <T> T deserialize(JsonNode currentNode, Class<T> wrapperClazz) {
        try {
            if (currentNode.isObject()) {
                return deserializeObject((JsonObjectNode) currentNode, wrapperClazz);
            }

            if (currentNode.isArray()) {
                return (T) deserializeArray((JsonArrayNode) currentNode, wrapperClazz);
            }

            throw new RuntimeException("Unexpected case");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T deserializeObject(JsonObjectNode objectNode, Class<T> wrapperClazz) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        T instance = wrapperClazz.getDeclaredConstructor().newInstance();
        Map<String, JsonNode> fields = objectNode.getFields();

        for (Map.Entry<String, JsonNode> fieldEntry : fields.entrySet()) {
            String fieldName = fieldEntry.getKey();
            JsonNode valueNode = fieldEntry.getValue();

            Field classField = wrapperClazz.getDeclaredField(fieldName);
            classField.setAccessible(true);

            Object fieldValue = deserializeField(valueNode, classField.getType());
            classField.set(instance, fieldValue);
        }

        return instance;
    }

    private Object deserializeArray(JsonArrayNode arrayNode, Class<?> arrayType) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        List<JsonNode> elements = arrayNode.getElements();
        if (arrayType.isArray()) {
            return deserializeToArray(elements, arrayType.getComponentType());
        }

        if (List.class.isAssignableFrom(arrayType)) {
            return deserializeToList(elements, arrayType);
        }

        throw new IllegalArgumentException("Unsupported array type: " + arrayType);
    }

    private Object deserializeField(JsonNode valueNode, Class<?> fieldType) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        if (valueNode.isObject()) {
            return deserializeObject((JsonObjectNode) valueNode, fieldType);
        }

        if (valueNode.isArray()) {
            return deserializeArray((JsonArrayNode) valueNode, fieldType);
        }

        return convertJsonNodeToPrimitive(valueNode, fieldType);
    }

    private Object deserializeToArray(List<JsonNode> elements, Class<?> componentType) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Object array = Array.newInstance(componentType, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            Array.set(array, i, deserializeField(elements.get(i), componentType));
        }
        return array;
    }

    private List<Object> deserializeToList(List<JsonNode> elements, Class<?> listType) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        List<Object> list = new ArrayList<>();
        for (JsonNode element : elements) {
            list.add(deserializeField(element, listType));
        }
        return list;
    }

    private Object convertJsonNodeToPrimitive(JsonNode node, Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(node.asText());
        }

        if (type == double.class || type == Double.class) {
            return Double.parseDouble(node.asText());
        }

        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(node.asText());
        }

        if (type == String.class) {
            return node.asText();
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}
