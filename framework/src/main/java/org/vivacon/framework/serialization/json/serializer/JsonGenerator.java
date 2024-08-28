package org.vivacon.framework.serialization.json.serializer;

import org.vivacon.framework.serialization.common.ResourceCleaner;
import org.vivacon.framework.serialization.common.StrGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class JsonGenerator implements Cloneable, StrGenerator {
    private Writer writer;
    private JsonSerializationFeatures features;
    private int indentLevel = 0;
    private static final String INDENT = "  ";
    private final Map<Integer, String> indentCache = new HashMap<>();

    public JsonGenerator(Writer writer, JsonSerializationFeatures features) {
        this.writer = writer;
        this.features = features;
        ResourceCleaner.register(this, writer);
    }

    public JsonGenerator(String initialStr, JsonSerializationFeatures features) {
        this(new StringWriter(), features);
        append(initialStr);
    }

    public JsonGenerator(JsonSerializationFeatures features) {
        this(new StringWriter(), features);
    }

    @Override
    public void writeStartObject() {
        write("{");
        increaseIndent();
        if (features.shouldPrintPrettyJson()) {
            writeNextLine();
        }
    }

    @Override
    public void writeEndObject() {
        decreaseIndent();
        if (features.shouldPrintPrettyJson()) {
            writeNextLine();
        }
        write("}");
    }

    @Override
    public void writeStartArray() {
        write("[");
        increaseIndent();
        if (features.shouldPrintPrettyJson()) {
            writeNextLine();
        }
    }

    @Override
    public void writeEndArray() {
        decreaseIndent();
        if (features.shouldPrintPrettyJson()) {
            writeNextLine();
        }
        write("]");
    }

    @Override
    public void writeFieldName(String name) {
        if (features.shouldPrintPrettyJson()) {
            writeIndentation();
        }
        write("\"" + name + "\":");
        if (features.shouldPrintPrettyJson()) {
            write(" ");
        }
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
        if (features.shouldPrintPrettyJson()) {
            writeNextLine();
        }
    }

    @Override
    public void writeNextLine() {
        if(features.shouldPrintPrettyJson()) {
            write("\n");
        }
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

    private void writeIndentation() {
        write(getCachedIndent());
    }

    private void increaseIndent() {
        indentLevel++;
    }

    private void decreaseIndent() {
        indentLevel--;
    }

    private String getCachedIndent() {
        return indentCache.computeIfAbsent(indentLevel, this::generateIndent);
    }

    private String generateIndent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append(INDENT);
        }
        return sb.toString();
    }
}
