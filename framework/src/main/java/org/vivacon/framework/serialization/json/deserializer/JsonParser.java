package org.vivacon.framework.serialization.json.deserializer;

import java.io.IOException;
import java.io.StringReader;

public class JsonParser {
    private StringReader reader;
    private int currentChar;

    public JsonParser(String json) {
        reader = new StringReader(json);
    }

    private int readNext() throws IOException {
        return reader.read();
    }

    public void readStartObject() throws IOException {
        if (readNext() != '{') {
            throw new IOException("Expected start of object");
        }
    }

    public void readEndObject() throws IOException {
        if (readNext() != '}') {
            throw new IOException("Expected end of object");
        }
    }

    public String readFieldName() throws IOException {
        // Simplified for demonstration purposes
        return readString();
    }

    public String readString() throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = readNext()) != '"') {
            sb.append((char) ch);
        }
        return sb.toString();
    }

    public Number readNumber() throws IOException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = readNext()) != ',' && ch != '}') {
            sb.append((char) ch);
        }
        return Double.parseDouble(sb.toString());
    }

    public void skipWhitespace() throws IOException {
        int ch;
        while ((ch = readNext()) == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
            // Skip whitespace
        }
        // Put back the last read character
        reader.reset();
    }
}

