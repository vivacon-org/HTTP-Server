package org.vivacon.framework.serialization.json;

import org.vivacon.framework.serialization.common.Deserializer;
import org.vivacon.framework.serialization.common.ObjectMapper;
import org.vivacon.framework.serialization.common.Serializer;
import org.vivacon.framework.serialization.json.deserializer.JsonDeserializationContainer;
import org.vivacon.framework.serialization.json.serializer.JsonSerializationContainer;
import org.vivacon.framework.serialization.json.serializer.JsonSerializationContext;
import org.vivacon.framework.serialization.json.serializer.JsonSerializer;

public class JsonMapper extends ObjectMapper {

    private final JsonSerializationContext context;

    public JsonMapper(Serializer standardStdJsonSerializer, Deserializer standardStdJsonDeserializer) {
        super(standardStdJsonSerializer, standardStdJsonDeserializer);
        context = new JsonSerializationContext();
    }

    public JsonMapper() {
        super(new JsonSerializationContainer(), new JsonDeserializationContainer());
        context = new JsonSerializationContext();
    }

    public <T> JsonSerializer<T> registerSerializer(Class<T> clazz, JsonSerializer<T> serializer) {
        return null;
    }
}
