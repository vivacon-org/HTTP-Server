package org.vivacon.framework.serialization.json.deserializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
import java.util.Map;

public class JsonParserTest {
    @Test
    public void testParseObject() throws Exception {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType.LEFT_BRACE, "{"));
        tokens.add(new Token(TokenType.STRING, "key1"));
        tokens.add(new Token(TokenType.COLON, ":"));
        tokens.add(new Token(TokenType.STRING, "value1"));
        tokens.add(new Token(TokenType.COMMA, ","));
        tokens.add(new Token(TokenType.STRING, "key2"));
        tokens.add(new Token(TokenType.COLON, ":"));
        tokens.add(new Token(TokenType.NUMBER, "123"));
        tokens.add(new Token(TokenType.RIGHT_BRACE, "}"));

        JsonParser parser = new JsonParser(tokens);
        JsonNode result = parser.parse();

        Map<String, JsonNode> expectedMap = new HashMap<>();
        expectedMap.put("key1", new JsonStringNode("value1"));
        expectedMap.put("key2", new JsonNumberNode(123));

        JsonObjectNode expectedObject = new JsonObjectNode(expectedMap);


        Assertions.assertEquals(expectedObject, result);
    }

    @Test
    public void testParseArray() throws Exception {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType.LEFT_BRACKET, "["));
        tokens.add(new Token(TokenType.STRING, "item1"));
        tokens.add(new Token(TokenType.COMMA, ","));
        tokens.add(new Token(TokenType.NUMBER, "456"));
        tokens.add(new Token(TokenType.RIGHT_BRACKET, "]"));

        JsonParser parser = new JsonParser(tokens);
        JsonNode result = parser.parse();

        ArrayList<JsonNode> expectedList = new ArrayList<>();
        expectedList.add(new JsonStringNode("item1"));
        expectedList.add(new JsonNumberNode(456));

        JsonArrayNode expectedArray = new JsonArrayNode(expectedList);

        Assertions.assertEquals(expectedArray, result);
    }

    @Test
    public void testParseString() throws Exception {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType.STRING, "stringValue"));

        JsonParser parser = new JsonParser(tokens);
        JsonNode result = parser.parse();

        Assertions.assertEquals(new JsonStringNode("stringValue"), result);
    }

    @Test
    public void testParseNumber() throws Exception {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType.NUMBER, "789"));

        JsonParser parser = new JsonParser(tokens);
        JsonNode result = parser.parse();

        Assertions.assertEquals(new JsonNumberNode(789), result);
    }

    @Test
    public void testParseBoolean() throws Exception {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType.BOOLEAN, "true"));

        JsonParser parser = new JsonParser(tokens);
        JsonNode result = parser.parse();

        Assertions.assertEquals(new JsonBooleanNode(true), result);
    }

    @Test
    public void testParseNull() throws Exception {
        ArrayList<Token> tokens = new ArrayList<>();
        tokens.add(new Token(TokenType.NULL, "null"));

        JsonParser parser = new JsonParser(tokens);
        JsonNode result = parser.parse();

        Assertions.assertEquals(JsonNullNode.getInstance(), result);
    }

    @Test
    public void testLargeJson() {
        String json = "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"age\": 30,\n" +
                "  \"isStudent\": false,\n" +
                "  \"address\": {\n" +
                "    \"street\": \"123 Elm Street\",\n" +
                "    \"city\": \"Springfield\",\n" +
                "    \"zipcode\": \"12345\"\n" +
                "  },\n" +
                "  \"courses\": [\n" +
                "    {\n" +
                "      \"courseName\": \"Mathematics\",\n" +
                "      \"grade\": \"A\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"courseName\": \"Science\",\n" +
                "      \"grade\": \"B\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"graduationYear\": null\n" +
                "}";

        Assertions.assertDoesNotThrow(() -> {

            JsonLexer lexer = new JsonLexer(json);
            ArrayList<Token> tokens = lexer.tokenize();
            JsonParser parser = new JsonParser(tokens);
            JsonNode actualJsonNode = parser.parse();
            
            Map<String, JsonNode> addressMap = new HashMap<>();
            addressMap.put("street", new JsonStringNode("123 Elm Street"));
            addressMap.put("city", new JsonStringNode("Springfield"));
            addressMap.put("zipcode", new JsonStringNode("12345"));

            ArrayList<JsonNode> courseList = new ArrayList<>();
            Map<String, JsonNode> course1 = new HashMap<>();
            course1.put("courseName", new JsonStringNode("Mathematics"));
            course1.put("grade", new JsonStringNode("A"));
            Map<String, JsonNode> course2 = new HashMap<>();
            course2.put("courseName", new JsonStringNode("Science"));
            course2.put("grade", new JsonStringNode("B"));
            courseList.add(new JsonObjectNode(course1));
            courseList.add(new JsonObjectNode(course2));

            Map<String, JsonNode> expectedMap = new HashMap<>();
            expectedMap.put("name", new JsonStringNode("John Doe"));
            expectedMap.put("age", new JsonNumberNode(30));
            expectedMap.put("isStudent", new JsonBooleanNode(false));
            expectedMap.put("address", new JsonObjectNode(addressMap));
            expectedMap.put("courses", new JsonArrayNode(courseList));
            expectedMap.put("graduationYear", JsonNullNode.getInstance());

            JsonObjectNode expectedJsonNode = new JsonObjectNode(expectedMap);

            Assertions.assertEquals(expectedJsonNode, actualJsonNode);
        });
    }
}
