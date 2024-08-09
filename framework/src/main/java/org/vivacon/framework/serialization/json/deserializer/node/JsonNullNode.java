package org.vivacon.framework.serialization.json.deserializer.node;

public class JsonNullNode implements JsonNode {

    private JsonNullNode() {
    }

    private static final JsonNullNode INSTANCE = new JsonNullNode();

    public static JsonNullNode getInstance() {
        return INSTANCE;
    }
}
