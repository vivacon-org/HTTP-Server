package org.vivacon.framework.serialization.json.deserializer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class JsonLexer {

    public enum TokenType {
        LEFT_BRACE, RIGHT_BRACE, LEFT_BRACKET, RIGHT_BRACKET, COLON, COMMA, STRING, NUMBER, BOOLEAN, NULL, EOF
    }

    public static class Token {

        public final TokenType type;

        public final String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    private final StringReader reader;
    private int currentChar;

    public JsonLexer(String input) throws IOException {
        this.reader = new StringReader(input);
        this.currentChar = reader.read();
    }

    public JsonLexer(StringReader input) throws IOException {
        this.reader = input;
        this.currentChar = reader.read();
    }

    public ArrayList<Token> tokenize() throws IOException {
        ArrayList<Token> tokens = new ArrayList<>();
        while (currentChar != -1) {

            if (currentChar == '\n') {
                currentChar = reader.read();
                continue;
            }

            if (Character.isWhitespace(currentChar)) {
                currentChar = reader.read();
                continue;
            }

            if (currentChar == '{') {
                tokens.add(new Token(TokenType.LEFT_BRACE, "{"));
                currentChar = reader.read();
                continue;
            }

            if (currentChar == '}') {
                tokens.add(new Token(TokenType.RIGHT_BRACE, "}"));
                currentChar = reader.read();
                continue;
            }

            if (currentChar == '[') {
                tokens.add(new Token(TokenType.LEFT_BRACKET, "["));
                currentChar = reader.read();
                continue;
            }

            if (currentChar == ']') {
                tokens.add(new Token(TokenType.RIGHT_BRACKET, "]"));
                currentChar = reader.read();
                continue;
            }

            if (currentChar == ':') {
                tokens.add(new Token(TokenType.COLON, ":"));
                currentChar = reader.read();
                continue;
            }

            if (currentChar == ',') {
                tokens.add(new Token(TokenType.COMMA, ","));
                currentChar = reader.read();
                continue;
            }

            if (currentChar == '"') {
                tokens.add(new Token(TokenType.STRING, parseString()));
                continue;
            }

            if (Character.isDigit(currentChar) || currentChar == '-') {
                tokens.add(new Token(TokenType.NUMBER, parseNumber()));
                continue;
            }

            if (currentChar == 't' || currentChar == 'f') {
                tokens.add(new Token(TokenType.BOOLEAN, parseBoolean()));
                continue;
            }

            if (currentChar == 'n') {
                tokens.add(new Token(TokenType.NULL, parseNull()));
                continue;
            }

            throw new IOException("Unexpected character: " + (char) currentChar);
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private String parseString() throws IOException {
        StringBuilder sb = new StringBuilder();
        currentChar = reader.read(); // Skip the opening quote
        while (currentChar != '"') {
            sb.append((char) currentChar);
            currentChar = reader.read();
        }
        currentChar = reader.read(); // Skip the closing quote
        return sb.toString();
    }

    private String parseNumber() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (Character.isDigit(currentChar) || currentChar == '-' || currentChar == '.') {
            sb.append((char) currentChar);
            currentChar = reader.read();
        }
        return sb.toString();
    }

    private String parseBoolean() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetter(currentChar)) {
            sb.append((char) currentChar);
            currentChar = reader.read();
        }

        String value = sb.toString().toLowerCase();
        if ("true".equals(value) || "false".equals(value)) {
            return sb.toString();
        }
        throw new IllegalArgumentException(String.format("Having a unexpected value %s in json", value));
    }

    private String parseNull() throws IOException {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetter(currentChar)) {
            sb.append((char) currentChar);
            currentChar = reader.read();
        }
        return sb.toString();
    }
}

