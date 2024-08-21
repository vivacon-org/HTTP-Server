package org.vivacon.framework.serialization.common;

import java.io.Reader;

public interface Deserializer {

    <T> T deserialize(String serializedString, Class<? extends T> expectedClass);

    <T> T deserialize(Reader inputReader, Class<? extends T> expectedClass);
}
