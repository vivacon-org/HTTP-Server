package org.vivacon.framework.serialization;

import java.io.Reader;

public class StandardJsonDeserializer implements JsonDeserializer {

    @Override
    public Object deserialize(String jsonString) {
        return deserialize(jsonString, Object.class);
    }

    @Override
    public <T> T deserialize(String jsonString, Class<? extends T> expectedClass) {
        JsonNode rootNode = JsonParser.parse(jsonString);
        Object deserializedInstance = ObjectConstructor.constructObject(rootNode.getChild("com.hung.TestClass"), expectedClass);
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
