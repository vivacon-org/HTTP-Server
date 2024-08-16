package org.vivacon.framework.serialization.json.deserializer;

import org.vivacon.framework.serialization.Deserializer;
import org.vivacon.framework.serialization.json.deserializer.node.JsonNode;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

public class JsonDeserializerContainer implements Deserializer {

    @Override
    public <T> T deserialize(String serializedString, Class<? extends T> expectedClass) {
        try (Reader reader = new StringReader(serializedString)) {
            return deserialize(reader, expectedClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(Reader inputReader, Class<? extends T> expectedClass) {
        try {
            JsonLexer lexer = new JsonLexer(inputReader);
            ArrayList<JsonLexer.Token> tokens = lexer.tokenize();

            JsonParser parser = new JsonParser(tokens);
            JsonNode jsonNode = parser.parse();

            StdJsonDeserializer deserializer = new StdJsonDeserializer();
            return deserializer.deserialize(jsonNode, expectedClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
