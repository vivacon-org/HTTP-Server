package org.vivacon.framework.serialization.common;

import java.io.Writer;

public interface Serializer {

    String serialize(Object obj, Writer writer);

    String serialize(Object obj);
}
