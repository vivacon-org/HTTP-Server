package org.vivacon.framework.serialization.json.serializer;

import org.vivacon.framework.serialization.common.Serializer;
import org.vivacon.framework.serialization.common.StrGenerator;

public class JsonSerializationContainer implements Serializer {

    private final JsonSerializationContext context;

    public JsonSerializationContainer(JsonSerializationContext context) {
        this.context = context;
    }

    @Override
    public String serialize(Object obj, StrGenerator gen) {
        if (!(gen instanceof JsonGenerator jsonGen)) {
            throw new RuntimeException("Invalid generator, expect JsonGenerator");
        }
        return new StdJsonSerializer().serialize(obj, jsonGen, context).generateString();
    }

    @Override
    public String serialize(Object obj) {
        JsonGenerator jsonGenerator = new JsonGenerator(context.getFeatures());
        return new StdJsonSerializer().serialize(obj, jsonGenerator, context).generateString();
    }
}
