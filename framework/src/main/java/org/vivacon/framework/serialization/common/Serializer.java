package org.vivacon.framework.serialization.common;

public interface Serializer {

    String serialize(Object obj, StrGenerator gen);
}
