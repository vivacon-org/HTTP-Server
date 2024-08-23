package org.vivacon.framework.serialization.common;

import org.vivacon.framework.serialization.json.deserializer.node.JsonNode;

import java.io.Reader;

public abstract class ObjectMapper implements Serializer, Deserializer {

    protected final Serializer serializationContainer;
    protected final Deserializer deserializationContainer;

    public ObjectMapper(Serializer standardStdJsonSerializer,
                        Deserializer standardStdJsonDeserializer) {
        this.serializationContainer = standardStdJsonSerializer;
        this.deserializationContainer = standardStdJsonDeserializer;
    }

    @Override
    public <T> T deserialize(String serializedString, Class<? extends T> expectedClass) {
        return deserializationContainer.deserialize(serializedString, expectedClass);
    }

    @Override
    public <T> T deserialize(Reader inputReader, Class<? extends T> expectedClass) {
        return deserializationContainer.deserialize(inputReader, expectedClass);
    }

    @Override
    public String serialize(Object obj) {
        return serializationContainer.serialize(obj);
    }

    @Override
    public String serialize(Object obj, StrGenerator gen) {
        return serializationContainer.serialize(obj, gen);
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