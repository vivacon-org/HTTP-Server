package org.vivacon.framework.serialization.json.serializer;

public class JsonSerializationFeatures {

    private boolean shouldIncludeClazzNameWrapper;

    private boolean shouldPrintPrettyJson;

    public JsonSerializationFeatures() {
        shouldPrintPrettyJson = true;
        shouldIncludeClazzNameWrapper = false;
    }

    public void enableIncludeClazzNameWrapper(boolean shouldIncludeClazzNameWrapper) {
        this.shouldIncludeClazzNameWrapper = shouldIncludeClazzNameWrapper;
    }

    public boolean shouldIncludeClazzNameWrapper() {
        return shouldIncludeClazzNameWrapper;
    }

    public void enablePrintPrettyJson(boolean shouldPrintPrettyJson) {
        this.shouldPrintPrettyJson = shouldPrintPrettyJson;
    }

    public boolean shouldPrintPrettyJson() {
        return shouldPrintPrettyJson;
    }
}
