package org.vivacon.framework.serialization;

import java.io.Reader;

public interface Deserializer {

    Object deserialize(String serializedString);

    <T> T deserialize(String serializedString, Class<? extends T> expectedClass);

    Object deserialize(Reader inputReader);

    Object deserialize(Reader inputReader, Class<?> expectedClass);
}
