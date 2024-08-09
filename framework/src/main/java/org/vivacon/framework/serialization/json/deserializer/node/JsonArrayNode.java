package org.vivacon.framework.serialization.json.deserializer.node;

import java.util.List;
import java.util.Objects;

public class JsonArrayNode implements JsonNode {
    private final List<JsonNode> values;

    public JsonArrayNode(List<JsonNode> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonArrayNode that = (JsonArrayNode) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}