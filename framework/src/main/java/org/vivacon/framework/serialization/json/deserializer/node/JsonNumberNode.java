package org.vivacon.framework.serialization.json.deserializer.node;

public class JsonNumberNode extends JsonNode {
    private final double value;

    public JsonNumberNode(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
