package org.vivacon.framework.serialization;

import java.util.Stack;

public class JsonParser {

    public static JsonNode parse(String jsonString) {
        JsonNode rootNode = new JsonNode();
        Stack<JsonNode> stack = new Stack<>();
        JsonNode currentNode = rootNode;
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        boolean inQuotes = false;
        boolean escape = false;
        boolean isKey = true;

        for (char ch : jsonString.toCharArray()) {
            if (ch == '"' && !escape) {
                inQuotes = !inQuotes;
                continue;
            }

            if (inQuotes) {
                if (ch == '\\') {
                    escape = !escape;
                    continue;
                }
                if (isKey) {
                    keyBuilder.append(ch);
                } else {
                    valueBuilder.append(ch);
                }
                escape = false;
                continue;
            }

            if (ch == '{') {
                JsonNode newNode = new JsonNode();
                currentNode.addChild(keyBuilder.toString(), newNode);
                stack.push(currentNode);
                currentNode = newNode;
                keyBuilder.setLength(0);
                isKey = true;
            } else if (ch == '}') {
                if (valueBuilder.length() > 0) {
                    currentNode.setValue(valueBuilder.toString().trim());
                    valueBuilder.setLength(0);
                }
                currentNode = stack.pop();
                isKey = true;
            } else if (ch == ':') {
                isKey = false;
            } else if (ch == ',') {
                if (valueBuilder.length() > 0) {
                    currentNode.setValue(valueBuilder.toString().trim());
                    valueBuilder.setLength(0);
                }
                isKey = true;
            } else if (!Character.isWhitespace(ch)) {
                if (isKey) {
                    keyBuilder.append(ch);
                } else {
                    valueBuilder.append(ch);
                }
            }
        }

        if (valueBuilder.length() > 0) {
            currentNode.setValue(valueBuilder.toString().trim());
        }

        return rootNode;
    }
}


