package org.vivacon.framework.serialization.json.deserializer.node;

public class JsonStringNode extends JsonNode {
    private final String value;

    public JsonStringNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}