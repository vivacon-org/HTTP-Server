package org.vivacon.framework.serialization.json.deserializer;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JsonLexerTest {

    @Test
    public void testTokenizeLeftBrace() throws IOException {
        JsonLexer lexer = new JsonLexer("{");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.LEFT_BRACE, tokens.get(0).type);
        assertEquals("{", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeRightBrace() throws IOException {
        JsonLexer lexer = new JsonLexer("}");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.RIGHT_BRACE, tokens.get(0).type);
        assertEquals("}", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeLeftBracket() throws IOException {
        JsonLexer lexer = new JsonLexer("[");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.LEFT_BRACKET, tokens.get(0).type);
        assertEquals("[", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeRightBracket() throws IOException {
        JsonLexer lexer = new JsonLexer("]");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.RIGHT_BRACKET, tokens.get(0).type);
        assertEquals("]", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeColon() throws IOException {
        JsonLexer lexer = new JsonLexer(":");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.COLON, tokens.get(0).type);
        assertEquals(":", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeComma() throws IOException {
        JsonLexer lexer = new JsonLexer(",");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.COMMA, tokens.get(0).type);
        assertEquals(",", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeString() throws IOException {
        JsonLexer lexer = new JsonLexer("\"hello\"");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.STRING, tokens.get(0).type);
        assertEquals("hello", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeNumber() throws IOException {
        JsonLexer lexer = new JsonLexer("1234.56");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.NUMBER, tokens.get(0).type);
        assertEquals("1234.56", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeNegativeNumber() throws IOException {
        JsonLexer lexer = new JsonLexer("-1234");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.NUMBER, tokens.get(0).type);
        assertEquals("-1234", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeBooleanTrue() throws IOException {
        JsonLexer lexer = new JsonLexer("true");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.BOOLEAN, tokens.get(0).type);
        assertEquals("true", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeBooleanFalse() throws IOException {
        JsonLexer lexer = new JsonLexer("false");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.BOOLEAN, tokens.get(0).type);
        assertEquals("false", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeNull() throws IOException {
        JsonLexer lexer = new JsonLexer("null");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(2, tokens.size());
        assertEquals(JsonLexer.TokenType.NULL, tokens.get(0).type);
        assertEquals("null", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(1).type);
    }

    @Test
    public void testTokenizeWhitespace() throws IOException {
        JsonLexer lexer = new JsonLexer("   { } ");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(3, tokens.size());
        assertEquals(JsonLexer.TokenType.LEFT_BRACE, tokens.get(0).type);
        assertEquals("{", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.RIGHT_BRACE, tokens.get(1).type);
        assertEquals("}", tokens.get(1).value);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(2).type);
    }

    @Test
    public void testTokenizeNewline() throws IOException {
        JsonLexer lexer = new JsonLexer("{\n}");
        List<JsonLexer.Token> tokens = lexer.tokenize();
        assertEquals(3, tokens.size());
        assertEquals(JsonLexer.TokenType.LEFT_BRACE, tokens.get(0).type);
        assertEquals("{", tokens.get(0).value);
        assertEquals(JsonLexer.TokenType.RIGHT_BRACE, tokens.get(1).type);
        assertEquals(JsonLexer.TokenType.EOF, tokens.get(2).type);
    }

    @Test
    public void testUnexpectedCharacter() throws IOException {
        JsonLexer lexer = new JsonLexer("#");
        IOException exception = assertThrows(IOException.class, lexer::tokenize);
        assertEquals("Unexpected character: #", exception.getMessage());
    }

    @Test
    public void testTokenizeLargeJson() throws IOException {
        String largeJson = "{\n" +
                "  \"name\": \"John\",\n" +
                "  \"age\": 30,\n" +
                "  \"isStudent\": false,\n" +
                "  \"address\": {\n" +
                "    \"street\": \"123 Main St\",\n" +
                "    \"city\": \"Anytown\"\n" +
                "  },\n" +
                "  \"courses\": [\n" +
                "    \"Math\",\n" +
                "    \"Science\"\n" +
                "  ],\n" +
                "  \"grades\": {\n" +
                "    \"Math\": 90,\n" +
                "    \"Science\": 85\n" +
                "  },\n" +
                "  \"nullField\": null\n" +
                "}";

        try (StringReader reader = new StringReader(largeJson)) {
            JsonLexer lexer = new JsonLexer(reader);
            List<JsonLexer.Token> tokens = lexer.tokenize();

            Assertions.assertNotNull(tokens);
            Assertions.assertFalse(tokens.isEmpty());

            // Validate a few specific tokens for correctness
            assertEquals(JsonLexer.TokenType.LEFT_BRACE, tokens.get(0).type);
            assertEquals("{", tokens.get(0).value);

            assertEquals(JsonLexer.TokenType.STRING, tokens.get(1).type);
            assertEquals("name", tokens.get(1).value);

            assertEquals(JsonLexer.TokenType.COLON, tokens.get(2).type);
            assertEquals(":", tokens.get(2).value);

            assertEquals(JsonLexer.TokenType.STRING, tokens.get(3).type);
            assertEquals("John", tokens.get(3).value);

            assertEquals(JsonLexer.TokenType.RIGHT_BRACE, tokens.get(tokens.size() - 2).type);
            // Check the EOF token
            assertEquals(JsonLexer.TokenType.EOF, tokens.get(tokens.size() - 1).type);
        }
    }
}
