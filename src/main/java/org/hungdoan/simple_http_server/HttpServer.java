package org.hungdoan.simple_http_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private static final int DEFAULT_PORT = 8080;
    private static final Logger LOG = LoggerFactory.getLogger(HttpServer.class);
    private int port;
    private Map<String, Map<Method, Handler>> pathHandlers;

    public HttpServer(int port) {
        this.port = port;
    }

    public HttpServer() {
        this.port = DEFAULT_PORT;
    }

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        try {
            LOG.info("Start the http server at port " + server.port);
            server.start();
        } catch (IOException e) {
            System.out.println("Something went wrong with the process of operating the http server");
        }
    }

    public void start() throws IOException {

        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
        Socket client;
        while((client = serverSocket.accept()) != null ){
            LOG.info("Receiving and starting the handle the request");
            SocketHandler socketHandler = new SocketHandler(client);
            Thread thread = new Thread(socketHandler);
            thread.start();
            LOG.info("Waiting for the next incoming request");
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        Map<Method, Handler> methodHandlers = pathHandlers.get(path);
        if (methodHandlers == null) {
            methodHandlers = new HashMap<>();
            methodHandlers.put(Method.valueOf(method), handler);
        }
        pathHandlers.put(path, methodHandlers);
    }
}
