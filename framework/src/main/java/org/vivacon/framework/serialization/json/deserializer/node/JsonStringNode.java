package org.vivacon.framework.serialization.json.deserializer.node;

import java.util.Objects;

public class JsonStringNode implements JsonNode {
    private final String value;

    public JsonStringNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonStringNode that = (JsonStringNode) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String asText() {
        return value;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }
}