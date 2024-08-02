package org.vivacon.framework.serialization;

public class ObjectMapper {

    private final Serializer standardJsonSerializer;
    private final Deserializer standardJsonDeserializer;

    public ObjectMapper(Serializer standardStdJsonSerializer,
                        Deserializer standardStdJsonDeserializer) {
        this.standardJsonSerializer = standardStdJsonSerializer;
        this.standardJsonDeserializer = standardStdJsonDeserializer;
    }
}