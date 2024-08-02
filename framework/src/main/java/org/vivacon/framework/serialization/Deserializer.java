package org.vivacon.framework.serialization;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public interface Deserializer {

    default Object deserialize(String serializedString) {
        try (Reader inputReader = new StringReader(serializedString)) {
            return deserialize(inputReader, Object.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    <T> T deserialize(String serializedString, Class<? extends T> expectedClass);

    default Object deserialize(Reader inputReader) {
        return deserialize(inputReader, Object.class);
    }

    <T> T deserialize(Reader inputReader, Class<? extends T> expectedClass);
}
