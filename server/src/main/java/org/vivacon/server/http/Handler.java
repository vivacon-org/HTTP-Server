package org.vivacon.server.http;

public interface Handler {

    Object handle(Request request, Response response);

}
