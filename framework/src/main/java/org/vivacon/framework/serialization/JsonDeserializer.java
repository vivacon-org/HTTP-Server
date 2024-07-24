package org.vivacon.framework.serialization;

import java.io.Reader;

public interface JsonDeserializer {

    Object deserialize(String jsonString);

    <T> T deserialize(String jsonString, Class<? extends T> expectedClass);

    Object deserialize(Reader inputReader);

    Object deserialize(Reader inputReader, Class<?> expectedClass);
}
