package org.vivacon.framework.serialization.json.serializer;

import org.vivacon.framework.serialization.StrGenerator;

import java.io.StringWriter;

public class JsonGenerator implements Cloneable, StrGenerator {
    private StringWriter writer;

    public JsonGenerator() {
        writer = new StringWriter();
    }

    public JsonGenerator(String initialStr) {
        writer = new StringWriter();
        writer.append(initialStr);
    }

    @Override
    public void writeStartObject() {
        writer.write("{");
    }

    @Override
    public void writeEndObject() {
        writer.write("}");
    }

    @Override
    public void writeFieldName(String name) {
        writer.write("\"" + name + "\":");
    }

    @Override
    public void writeString(String value) {
        writer.write("\"" + value + "\"");
    }

    @Override
    public void writeNumber(Number value) {
        writer.write(value.toString());
    }

    @Override
    public void writeNull() {
        writer.write("null");
    }

    @Override
    public void writeSeparator() {
        writer.write(",");
    }

    @Override
    public void writeNextLine() {
        writer.write("\n");
    }

    @Override
    public String generateString() {
        return writer.toString();
    }

    @Override
    public JsonGenerator clone() {
        try {
            JsonGenerator cloned = (JsonGenerator) super.clone();
            cloned.writer = new StringWriter();
            cloned.writer.append(this.writer.toString());
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }
}
