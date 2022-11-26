package org.hungdoan.simple_http_server;

public interface Handler {

    Object handle(Request request, Response response);
}
