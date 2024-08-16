package org.vivacon.framework.serialization.json.deserializer.node;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class JsonArrayNode implements JsonNode {
    private final List<JsonNode> elements;

    public JsonArrayNode(List<JsonNode> elements) {
        this.elements = elements;
    }

    public List<JsonNode> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonArrayNode that = (JsonArrayNode) o;
        return Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public String asText() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public boolean isObject() {
        return false;
    }
}