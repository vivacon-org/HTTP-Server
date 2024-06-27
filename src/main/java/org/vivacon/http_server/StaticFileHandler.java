package org.vivacon.http_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivacon.http_server.exception.StaticResourceNotFound;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class StaticFileHandler implements Handler {

    private static final Logger LOG = LoggerFactory.getLogger(StaticFileHandler.class);

    @Override
    public Object handle(Request request, Response response) throws StaticResourceNotFound {
        String path = request.getPath();
        String content;
        try {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader("./src/main/resources/" + path))) {
                content = bufferedReader.lines().collect(Collectors.joining("\n"));
            }
            response.setResponseCode(200, "OK");
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
        } catch (IOException e) {
            LOG.info("Can not find any suitable static file to serve for !" + request.getPath());
            throw new StaticResourceNotFound();
        }
        return content;
    }

}
