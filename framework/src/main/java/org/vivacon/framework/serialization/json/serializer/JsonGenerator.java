package org.vivacon.framework.serialization.json.serializer;

import org.vivacon.framework.serialization.StrGenerator;

import java.io.StringWriter;

public class JsonGenerator implements StrGenerator {
    private StringWriter writer;

    public JsonGenerator() {
        writer = new StringWriter();
    }

    public void writeStartObject() {
        writer.write("{");
    }

    public void writeEndObject() {
        writer.write("}");
    }

    public void writeFieldName(String name) {
        writer.write("\"" + name + "\":");
    }

    public void writeString(String value) {
        writer.write("\"" + value + "\"");
    }

    public void writeNumber(Number value) {
        writer.write(value.toString());
    }

    public void writeNull() {
        writer.write("null");
    }

    public String generateString() {
        return writer.toString();
    }
}
