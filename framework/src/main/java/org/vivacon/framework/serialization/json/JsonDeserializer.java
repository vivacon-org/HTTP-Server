package org.vivacon.framework.serialization.json;

import org.vivacon.framework.serialization.Deserializer;

import java.io.Reader;

public class JsonDeserializer implements Deserializer {

    @Override
    public Object deserialize(String serializedString) {
        return deserialize(serializedString, Object.class);
    }

    @Override
    public <T> T deserialize(String serializedString, Class<? extends T> expectedClass) {
        JsonNode rootNode = JsonParser.parse(serializedString);
        Object deserializedInstance = JsonInstanceConstructor.constructObject(rootNode.getChild("com.hung.TestClass"), expectedClass);
        return (T) deserializedInstance;
    }

    @Override
    public Object deserialize(Reader inputReader) {
        return deserialize(inputReader, Object.class);
    }

    @Override
    public Object deserialize(Reader inputReader, Class<?> expectedClass) {
        throw new RuntimeException("Still not implement");
    }
}
