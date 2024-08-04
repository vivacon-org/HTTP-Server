package org.vivacon.server.http;

public enum Method {

    GET("get"), POST("post");

    private String rawName;

    Method(String rawName) {
        this.rawName = rawName;
    }

    public Method get(String name) {
        String lowerCaseName = name.trim().toLowerCase();
        return Method.valueOf(lowerCaseName);
    }

}
