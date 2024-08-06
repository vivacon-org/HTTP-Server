package org.vivacon.framework.serialization.json.deserializer.node;

import java.util.Map;

public class JsonObjectNode extends JsonNode {
    private final Map<String, JsonNode> fields;

    public JsonObjectNode(Map<String, JsonNode> fields) {
        this.fields = fields;
    }

    public Map<String, JsonNode> getFields() {
        return fields;
    }
}