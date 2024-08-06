package org.vivacon.framework.serialization.json.deserializer.node;

import java.util.List;

public class JsonArrayNode extends JsonNode {
    private final List<JsonNode> values;

    public JsonArrayNode(List<JsonNode> values) {
        this.values = values;
    }

    public List<JsonNode> getValues() {
        return values;
    }
}