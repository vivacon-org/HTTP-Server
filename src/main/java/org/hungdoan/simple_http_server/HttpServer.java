package org.hungdoan.simple_http_server;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public static final int PORT = 8080;

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        try {
            server.start();
        } catch (IOException e) {
            System.out.println("Something went wrong with the process of operating the http server");
        }
    }

    public void start() throws IOException {

        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(PORT);
        Socket client;
        while( (client = serverSocket.accept()) != null ){
            SocketHandler socketHandler = new SocketHandler(client);
            Thread thread = new Thread(socketHandler);
            thread.start();
        }
    }
}
