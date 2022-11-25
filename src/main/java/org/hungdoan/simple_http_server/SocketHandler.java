package org.hungdoan.simple_http_server;

import java.net.Socket;

public class SocketHandler implements Runnable {

    private Socket client;

    public SocketHandler(Socket client){
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("Something is running");
    }
}
