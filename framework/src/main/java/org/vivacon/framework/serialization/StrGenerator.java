package org.vivacon.framework.serialization;

public interface StrGenerator {

    void writeStartObject();

    void writeEndObject();

    void writeFieldName(String name);

    void writeString(String value);

    void writeNumber(Number value);

    void writeNull();

    void writeSeparator();

    void writeNextLine();

    String generateString();
}
