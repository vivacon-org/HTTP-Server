package org.vivacon.http_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivacon.framework.Engine;
import org.vivacon.http_server.exception.MethodHandlerNotFound;
import org.vivacon.http_server.exception.PathHandlerNotFound;

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
    private static Map<String, Map<Method, Handler>> pathHandlers = new HashMap<>();

    public static Engine engine;

    public HttpServer(int port) {
        this.port = port;
    }

    public HttpServer() {
        this.port = DEFAULT_PORT;
    }

    public static void main(String[] args) {
        engine = new Engine();
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
        StaticFileHandler staticFileHandler = new StaticFileHandler();
        addHandler("/index.html", Method.GET, staticFileHandler);
        addHandler("/", Method.GET, staticFileHandler);

        Socket client;
        while ((client = serverSocket.accept()) != null) {
            LOG.info("Receiving and starting the handle the request");
            SocketHandler socketHandler = new SocketHandler(client);
            Thread thread = new Thread(socketHandler);
            thread.start();
            LOG.info("Waiting for the next incoming request");
        }
    }

    private static void addHandler(String path, Method method, Handler handler) {
        Map<Method, Handler> methodHandlers = pathHandlers.get(path);
        if (methodHandlers == null) {
            methodHandlers = new HashMap<>();
            methodHandlers.put(method, handler);
        }
        pathHandlers.put(path, methodHandlers);
    }

    public static Handler getHandler(String path, Method method) throws PathHandlerNotFound, MethodHandlerNotFound {
        Map<Method, Handler> methodHandlerMap = pathHandlers.get(path);
        if (methodHandlerMap == null) {
            throw new PathHandlerNotFound();
        }
        Handler methodHandler = methodHandlerMap.get(method);
        if (methodHandler == null) {
            throw new MethodHandlerNotFound();
        }
        return methodHandler;
    }
}
