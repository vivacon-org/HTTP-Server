package org.vivacon.http_server;

public interface Handler {

    Object handle(Request request, Response response);

}
