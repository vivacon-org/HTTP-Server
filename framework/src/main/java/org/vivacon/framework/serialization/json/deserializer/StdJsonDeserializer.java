package org.vivacon.framework.serialization.json.deserializer;

import org.vivacon.framework.serialization.Deserializer;

import java.io.Reader;

public class StdJsonDeserializer implements Deserializer {

    @Override
    public <T> T deserialize(String serializedString, Class<? extends T> expectedClass) {
        return null;
    }

    @Override
    public <T> T deserialize(Reader inputReader, Class<? extends T> expectedClass) {
        return null;
    }
}
