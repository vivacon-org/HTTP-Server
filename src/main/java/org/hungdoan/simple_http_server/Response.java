package org.hungdoan.simple_http_server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Response {
    private final Socket client;
    public Response(Socket client) {
        this.client = client;
    }

    public void backToClient() throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        bufferedWriter.write("Hello there");
        bufferedWriter.close();
    }
}
