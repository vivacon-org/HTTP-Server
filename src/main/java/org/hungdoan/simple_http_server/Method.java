package org.hungdoan.simple_http_server;

public enum Method {

    GET("get"), POST("post");

    private String rawName;

    Method(String rawName) {
        this.rawName = rawName;
    }

    public Method get(String name){
        String lowerCaseName = name.strip().toLowerCase();
        return Method.valueOf(lowerCaseName);
    }
}
