package org.vivacon.framework.serialization.json.serializer;

public class JsonSerializationFeatures {

    private boolean shouldIncludeClazzNameWrapper;

    private boolean shouldPrintPrettyJson;

    public JsonSerializationFeatures() {
        shouldPrintPrettyJson = true;
        shouldIncludeClazzNameWrapper = false;
    }

    public void shouldIncludeClazzNameWrapper(boolean shouldIncludeClazzNameWrapper) {
        this.shouldIncludeClazzNameWrapper = shouldIncludeClazzNameWrapper;
    }

    public void shouldPrintPrettyJson(boolean shouldPrintPrettyJson) {
        this.shouldPrintPrettyJson = shouldPrintPrettyJson;
    }
}
