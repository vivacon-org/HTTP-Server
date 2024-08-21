package org.vivacon.framework.serialization.json.serializer;

import java.util.Map;

public class JsonSerializationContext {

    private Map<Class<?>, JsonSerializer<?>> clazzToSerializer;

    private JsonSerializationFeatures features;
}
