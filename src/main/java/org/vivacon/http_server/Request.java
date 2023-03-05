package org.vivacon.http_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Request {

    private static final Logger LOG = LoggerFactory.getLogger(Request.class);
    private Socket client;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private String path;
    private String verb;
    private String version;

    public InputStream getBody() throws IOException {
        //return new HttpInputStream(null, headers);
        return null;
    }

    public Request(Socket client) {
        this.client = client;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getPath() {
        return path;
    }

    public Method getVerb() {
        return Method.valueOf(verb);
    }

    public String getVersion() {
        return version;
    }

    public boolean parse() throws IOException {
        InputStream inputStream = client.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String initialLine = bufferedReader.readLine();
        StringTokenizer tokenizer = new StringTokenizer(initialLine);
        LOG.debug("Initial line of the request : " + initialLine);

        //VERB PATH VERSION
        String[] components = new String[3];
        for (int i = 0; i < components.length; i++) {
            if (tokenizer.hasMoreTokens()) {
                components[i] = tokenizer.nextToken();
            } else {
                LOG.info("Malformed http request in the initial line");
                return false;
            }
        }

        // consume headers, path, verb and http version
        consumeHeaders(bufferedReader, initialLine);
        consumePath(components);
        verb = components[0];
        version = components[2].substring(components[2].indexOf("/") + 1);

        LOG.info("Request info: " + this);
        return true;
    }

    private boolean consumePath(String[] components) {
        String originalPath = components[1];
        int indexOfQueryCharacter = originalPath.indexOf("?");

        // does it contains any request query param ?
        if (indexOfQueryCharacter == -1) {
            path = originalPath;
        } else {
            path = originalPath.substring(0, indexOfQueryCharacter);
            parseQueryParameters(originalPath.substring(indexOfQueryCharacter + 1));
        }
        if ("/".equals(path)) {
            path = "/index.html";
        }
        return path != null && path.isEmpty() == false;
    }

    private boolean consumeHeaders(BufferedReader bufferedReader, String initialLine) throws IOException {
        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null) {
            LOG.debug("One header line of the request : " + headerLine);
            if (headerLine.length() == 0) {
                break;
            }
            int separator = headerLine.indexOf(":");
            if (separator == -1) {
                return false;
            }
            headers.put(headerLine.substring(0, separator),
                    headerLine.substring(separator + 1));
        }
        return true;
    }

    private void parseQueryParameters(String queryString) {
        for (String parameter : queryString.split("&")) {
            int separator = parameter.indexOf('=');
            if (separator > -1) {
                queryParams.put(parameter.substring(0, separator),
                        parameter.substring(separator + 1));
            } else {
                queryParams.put(parameter, null);
            }
        }
    }

    @Override
    public String toString() {
        return "Request{" +
                "client=" + client +
                ", headers=" + headers +
                ", queryParams=" + queryParams +
                ", path='" + path + '\'' +
                ", verb='" + verb + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
