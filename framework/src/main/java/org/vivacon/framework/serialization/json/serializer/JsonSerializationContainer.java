package org.vivacon.framework.serialization.json.serializer;

import org.vivacon.framework.serialization.common.Serializer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class JsonSerializationContainer implements Serializer {

    private final JsonSerializationContext context;

    public JsonSerializationContainer(JsonSerializationContext context) {
        this.context = context;
    }

    @Override
    public String serialize(Object obj, Writer writer) {
        JsonGenerator jsonGenerator = new JsonGenerator(writer, context.getFeatures());
        return new StdJsonSerializer().serialize(obj, jsonGenerator, context).generateString();
    }

    @Override
    public String serialize(Object obj) {
        try (Writer writer = new StringWriter()) {
            return serialize(obj, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
