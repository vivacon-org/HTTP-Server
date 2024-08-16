package org.vivacon.framework.serialization.json.deserializer.node;

import java.util.Objects;

public class JsonBooleanNode implements JsonNode {
    private final boolean value;

    public JsonBooleanNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonBooleanNode that = (JsonBooleanNode) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String asText() {
        return String.valueOf(value);
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