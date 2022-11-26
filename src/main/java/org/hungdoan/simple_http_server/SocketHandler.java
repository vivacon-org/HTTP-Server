package org.hungdoan.simple_http_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;

public class SocketHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(SocketHandler.class);
    private Socket client;

    public SocketHandler(Socket client){
        this.client = client;
    }

    @Override
    public void run() {
        try {
            Request request = new Request(client);

            Response response = new Response(client);
            response.backToClient();
        }
        catch (Exception ex){
            LOG.info("Error when parsing the request");
        }
    }
}
