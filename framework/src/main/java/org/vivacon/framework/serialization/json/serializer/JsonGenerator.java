package org.vivacon.framework.serialization.json.serializer;

import org.vivacon.framework.serialization.common.ResourceCleaner;
import org.vivacon.framework.serialization.common.StrGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class JsonGenerator implements Cloneable, StrGenerator {
    private Writer writer;

    public JsonGenerator(String initialStr) {
        writer = new StringWriter();
        append(initialStr);
        ResourceCleaner.register(this, writer);
    }

    public JsonGenerator(Writer writer) {
        this.writer = writer;
        ResourceCleaner.register(this, writer);
    }

    @Override
    public void writeStartObject() {
        write("{");
    }

    @Override
    public void writeEndObject() {
        write("}");
    }

    @Override
    public void writeFieldName(String name) {
        write("\"" + name + "\":");
    }

    @Override
    public void writeString(String value) {
        write("\"" + value + "\"");
    }

    @Override
    public void writeNumber(Number value) {
        write(value.toString());
    }

    @Override
    public void writeNull() {
        write("null");
    }

    @Override
    public void writeSeparator() {
        write(",");
    }

    @Override
    public void writeNextLine() {
        write("\n");
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
        } catch (CloneNotSupportedException | IOException e) {
            throw new AssertionError("Cloning not supported", e);
        }
    }

    private void append(String str) {
        try {
            writer = writer.append(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(String str) {
        try {
            writer.write(str);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
