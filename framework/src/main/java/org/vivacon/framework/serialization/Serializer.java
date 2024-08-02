package org.vivacon.framework.serialization;

public interface Serializer {

    String serialize(Object obj, StrGenerator gen);
}
