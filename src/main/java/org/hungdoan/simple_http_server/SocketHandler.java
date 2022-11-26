package org.hungdoan.simple_http_server;

import org.hungdoan.simple_http_server.exception.MethodHandlerNotFound;
import org.hungdoan.simple_http_server.exception.PathHandlerNotFound;
import org.hungdoan.simple_http_server.exception.StaticResourceNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class SocketHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SocketHandler.class);
    private Socket client;

    public SocketHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        Response response = null;
        try {
            Request request = new Request(client);
            response = new Response(client);
            if (request.parse() == false) {
                response.respond(400, "Bad request");
                return;
            }
            Handler handler = HttpServer.getHandler(request.getPath(), request.getVerb());
            Object finalResult = handler.handle(request, response);
            response.setBody(finalResult.toString());
            response.send();
        } catch (PathHandlerNotFound | StaticResourceNotFound e) {
            response.respond(404, "The resource is not found");
        } catch (MethodHandlerNotFound e) {
            response.respond(403, "Method is not supported");
        } catch (Exception e) {
            response.respond(500, "Internal server error");
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                LOG.error("Can not close the connection to client");
            }
        }
    }
}
