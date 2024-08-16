package org.vivacon.framework.serialization.json.deserializer.node;

import java.util.Objects;

public class JsonNumberNode implements JsonNode {
    private final double value;

    public JsonNumberNode(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonNumberNode that = (JsonNumberNode) o;
        return Double.compare(value, that.value) == 0;
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
