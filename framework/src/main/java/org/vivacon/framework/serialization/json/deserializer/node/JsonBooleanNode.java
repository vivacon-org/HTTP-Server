package org.vivacon.framework.serialization.json.deserializer.node;

public class JsonBooleanNode extends JsonNode {
    private final boolean value;

    public JsonBooleanNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}