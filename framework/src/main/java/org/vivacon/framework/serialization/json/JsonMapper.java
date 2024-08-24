package org.vivacon.framework.serialization.json;

import org.vivacon.framework.serialization.common.Deserializer;
import org.vivacon.framework.serialization.common.ObjectMapper;
import org.vivacon.framework.serialization.common.Serializer;
import org.vivacon.framework.serialization.json.deserializer.JsonDeserializationContainer;
import org.vivacon.framework.serialization.json.serializer.JsonSerializationContainer;
import org.vivacon.framework.serialization.json.serializer.JsonSerializationContext;
import org.vivacon.framework.serialization.json.serializer.JsonSerializationFeatures;
import org.vivacon.framework.serialization.json.serializer.JsonSerializer;

public class JsonMapper extends ObjectMapper {
    private final JsonSerializationContext context;

    public JsonMapper() {
        this(new JsonSerializationContext());
    }

    public JsonMapper(JsonSerializationContext context) {
        super(new JsonSerializationContainer(context), new JsonDeserializationContainer());
        this.context = context;
    }

    public JsonMapper(Serializer standardStdJsonSerializer, Deserializer standardStdJsonDeserializer) {
        super(standardStdJsonSerializer, standardStdJsonDeserializer);
        context = new JsonSerializationContext();
    }

    public <T> JsonSerializer registerSerializer(Class<T> clazz, JsonSerializer serializer) {
        context.registerSerializer(clazz, serializer);
        return null;
    }

    public void setSerializationSettings(JsonSerializationFeatures features) {
        context.setFeatures(features);
    }
}
