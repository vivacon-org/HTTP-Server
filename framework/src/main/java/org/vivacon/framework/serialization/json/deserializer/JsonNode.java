package org.vivacon.framework.serialization.json.deserializer;

import java.util.HashMap;
import java.util.Map;

public class JsonNode {
    private final Map<String, JsonNode> children = new HashMap<>();
    private final String key;
    private final String value;

    public void addChild(String key, JsonNode child) {
        children.put(key, child);
    }

    public JsonNode getChild(String key) {
        return children.get(key);
    }

    public JsonNode(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Map<String, JsonNode> getChildren() {
        return children;
    }

    private boolean isLeaf() {

        if (value != null) {
            return true;
        }

        if (!children.isEmpty()) {
            return false;
        }

        throw new RuntimeException("Invalid node");
    }

    @Override
    public String toString() {
        if (isLeaf()) {
            return "\"" + value + "\"";
        } else {
            StringBuilder sb = new StringBuilder("{");
            for (Map.Entry<String, JsonNode> entry : children.entrySet()) {
                if (sb.length() > 1) {
                    sb.append(",");
                }
                sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue().toString());
            }
            sb.append("}");
            return sb.toString();
        }
    }
}