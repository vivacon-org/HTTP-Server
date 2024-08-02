package org.vivacon.framework.serialization.json.serializer;

public interface JsonSerializer {

    void serialize(Object obj, JsonGenerator gen);
}
