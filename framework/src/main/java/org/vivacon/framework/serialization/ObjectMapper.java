package org.vivacon.framework.serialization;

import org.vivacon.framework.serialization.json.JsonDeserializer;
import org.vivacon.framework.serialization.json.JsonSerializer;

public class ObjectMapper {

    private final Serializer standardJsonSerializer;
    private final Deserializer standardJsonDeserializer;
    
    public ObjectMapper(JsonSerializer standardJsonSerializer, JsonDeserializer standardJsonDeserializer) {
        this.standardJsonSerializer = standardJsonSerializer;
        this.standardJsonDeserializer = standardJsonDeserializer;
    }
}