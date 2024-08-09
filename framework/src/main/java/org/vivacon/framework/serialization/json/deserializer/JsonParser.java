package org.vivacon.framework.serialization.json.deserializer;

import org.vivacon.framework.serialization.json.deserializer.JsonLexer.Token;
import org.vivacon.framework.serialization.json.deserializer.JsonLexer.TokenType;
import org.vivacon.framework.serialization.json.deserializer.node.JsonArrayNode;
import org.vivacon.framework.serialization.json.deserializer.node.JsonBooleanNode;
import org.vivacon.framework.serialization.json.deserializer.node.JsonNode;
import org.vivacon.framework.serialization.json.deserializer.node.JsonNullNode;
import org.vivacon.framework.serialization.json.deserializer.node.JsonNumberNode;
import org.vivacon.framework.serialization.json.deserializer.node.JsonObjectNode;
import org.vivacon.framework.serialization.json.deserializer.node.JsonStringNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {
    private final ArrayList<Token> tokens;
    private int index = 0;

    public JsonParser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    private Token currentToken() {
        return tokens.get(index);
    }

    private void consumeToken() {
        index++;
    }

    public JsonNode parse() throws Exception {
        Token token = currentToken();
        if (token.type == TokenType.LEFT_BRACE) {
            return parseObject();
        }

        if (token.type == TokenType.LEFT_BRACKET) {
            return parseArray();
        }
        return parseValue();
    }

    private JsonNode parseObject() throws Exception {
        Map<String, JsonNode> map = new HashMap<>();
        consumeToken(); // Consume LEFT_BRACE
        while (currentToken().type != TokenType.RIGHT_BRACE) {
            if (currentToken().type != TokenType.STRING) {
                throw new Exception("Expected STRING token for key.");
            }
            String key = currentToken().value;
            consumeToken(); // Consume STRING
            if (currentToken().type != TokenType.COLON) {
                throw new Exception("Expected COLON token.");
            }
            consumeToken(); // Consume COLON
            JsonNode value = parseValue();
            map.put(key, value);
            if (currentToken().type == TokenType.COMMA) {
                consumeToken(); // Consume COMMA
            }
        }
        consumeToken(); // Consume RIGHT_BRACE
        return new JsonObjectNode(map);
    }

    private JsonNode parseArray() throws Exception {
        List<JsonNode> list = new ArrayList<>();
        consumeToken(); // Consume LEFT_BRACKET
        while (currentToken().type != TokenType.RIGHT_BRACKET) {
            JsonNode value = parseValue();
            list.add(value);
            if (currentToken().type == TokenType.COMMA) {
                consumeToken(); // Consume COMMA
            }
        }
        consumeToken(); // Consume RIGHT_BRACKET
        return new JsonArrayNode(list);
    }

    private JsonNode parseValue() throws Exception {
        Token token = currentToken();
        if (token.type == TokenType.STRING) {
            consumeToken();
            return new JsonStringNode(token.value);
        }

        if (token.type == TokenType.NUMBER) {
            consumeToken();
            return new JsonNumberNode(Double.parseDouble(token.value));
        }

        if (token.type == TokenType.BOOLEAN) {
            consumeToken();
            return new JsonBooleanNode(Boolean.parseBoolean(token.value));
        }

        if (token.type == TokenType.NULL) {
            consumeToken();
            return JsonNullNode.getInstance();
        }

        if (token.type == TokenType.LEFT_BRACE) {
            return parseObject();
        }

        if (token.type == TokenType.LEFT_BRACKET) {
            return parseArray();
        }
        throw new Exception("Unexpected token: " + token);
    }
}
