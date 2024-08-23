package org.vivacon.framework.serialization.json.serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JsonSerializationContext {

    private final Map<Class<?>, JsonSerializer> clazzToSerializer;

    private JsonSerializationFeatures features;

    public JsonSerializationContext() {
        clazzToSerializer = new HashMap<>();
        features = new JsonSerializationFeatures();
    }

    public void registerSerializer(Class<?> clazz, JsonSerializer serializer) {
        clazzToSerializer.put(clazz, serializer);
    }

    public Optional<JsonSerializer> findSerializer(Class<?> clazz) {
        return Optional.of(clazzToSerializer.get(clazz));
    }

    public void setFeatures(JsonSerializationFeatures features) {
        this.features = features;
    }

    public JsonSerializationFeatures getFeatures() {
        return features;
    }
}
