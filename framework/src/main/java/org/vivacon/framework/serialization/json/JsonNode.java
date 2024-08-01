package org.vivacon.framework.serialization.json;

import java.util.HashMap;
import java.util.Map;

public class JsonNode {
    private final Map<String, JsonNode> children = new HashMap<>();
    private String key;
    private String value;
    private boolean isLeaf;

    public void addChild(String key, JsonNode child) {
        children.put(key, child);
    }

    public JsonNode getChild(String key) {
        return children.get(key);
    }

    public JsonNode(String key, String value) {
        this.key = key;
        this.value = value;
        this.isLeaf = true;
    }

    public String getValue() {
        return value;
    }

    public Map<String, JsonNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        if (isLeaf) {
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