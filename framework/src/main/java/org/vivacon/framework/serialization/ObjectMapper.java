package org.vivacon.framework.serialization;

public class ObjectMapper {

    private final StandardJsonSerializer standardJsonSerializer;
    private final StandardJsonDeserializer standardJsonDeserializer;

    public ObjectMapper() {
        this.standardJsonSerializer = new StandardJsonSerializer();
        this.standardJsonDeserializer = new StandardJsonDeserializer();
    }

    public ObjectMapper(StandardJsonSerializer standardJsonSerializer, StandardJsonDeserializer standardJsonDeserializer) {
        this.standardJsonSerializer = standardJsonSerializer;
        this.standardJsonDeserializer = standardJsonDeserializer;
    }
}