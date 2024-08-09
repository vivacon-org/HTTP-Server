package org.vivacon.framework.serialization.json.deserializer.node;

import java.util.Map;
import java.util.Objects;

public class JsonObjectNode implements JsonNode {
    private final Map<String, JsonNode> fields;

    public JsonObjectNode(Map<String, JsonNode> fields) {
        this.fields = fields;
    }

    public Map<String, JsonNode> getFields() {
        return fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonObjectNode that = (JsonObjectNode) o;
        return Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }
}