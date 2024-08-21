package org.vivacon.framework.serialization.json.serializer;

import org.vivacon.framework.serialization.common.Serializer;
import org.vivacon.framework.serialization.common.StrGenerator;

public class JsonSerializationContainer implements Serializer {

    @Override
    public String serialize(Object obj, StrGenerator gen) {
        if (!(gen instanceof JsonGenerator jsonGen)) {
            throw new RuntimeException("Invalid generator, expect JsonGenerator");
        }
        return new StdJsonSerializer().serialize(obj, jsonGen, new JsonSerializationContext()).generateString();
    }
}
