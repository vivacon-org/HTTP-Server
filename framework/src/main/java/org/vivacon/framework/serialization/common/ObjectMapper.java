package org.vivacon.framework.serialization.common;

import org.vivacon.framework.serialization.json.deserializer.node.JsonNode;

import java.io.Reader;

public abstract class ObjectMapper implements Serializer, Deserializer {

    private final Serializer standardJsonSerializer;
    private final Deserializer standardJsonDeserializer;

    public ObjectMapper(Serializer standardStdJsonSerializer,
                        Deserializer standardStdJsonDeserializer) {
        this.standardJsonSerializer = standardStdJsonSerializer;
        this.standardJsonDeserializer = standardStdJsonDeserializer;
    }

    @Override
    public <T> T deserialize(String serializedString, Class<? extends T> expectedClass) {
        return standardJsonDeserializer.deserialize(serializedString, expectedClass);
    }

    @Override
    public <T> T deserialize(Reader inputReader, Class<? extends T> expectedClass) {
        return standardJsonDeserializer.deserialize(inputReader, expectedClass);
    }

    @Override
    public String serialize(Object obj, StrGenerator gen) {
        return standardJsonSerializer.serialize(obj, gen);
    }

    public JsonNode readTree(Reader inputReader) {
        // TODO
        return null;
    }

    public JsonNode readTree(String inputReader) {
        // TODO
        return null;
    }
}