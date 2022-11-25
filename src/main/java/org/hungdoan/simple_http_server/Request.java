package org.hungdoan.simple_http_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;

public class Request {

    private Socket client;

    private Map<String, String> headers;

    private Map<String, String> queryParams;

    private String path;

    private String fullUrl;

    public Request(Socket client) {
        this.client = client;
    }

    private boolean parse() throws IOException {
        InputStream inputStream = client.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String initialLine = bufferedReader.readLine();

        StringTokenizer tokenizer = new StringTokenizer(initialLine);
        //VERB PATH VERSION
        String[] components = new String[3];
        for (int i = 0; i < components.length; i++) {
            if (tokenizer.hasMoreTokens()) {
                components[i] = tokenizer.nextToken();
            } else {
                System.out.println("Malformed http request");
                return false;
            }
        }

        String method = components[0];
        String version = components[2];

        //consume the header
        while (true) {
            String headerLine = bufferedReader.readLine();
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

        //consume path
        fullUrl = components[1];
        int indexOfQueryCharacter = fullUrl.indexOf("?");

        // does it contains any request query param ?
        if (indexOfQueryCharacter == -1) {
            path = fullUrl;
        } else {
            path = fullUrl.substring(0, indexOfQueryCharacter);
            parseQueryParameters(fullUrl.substring(indexOfQueryCharacter + 1));
        }

        if ("/".equals(path)) {
            path = "/index.html";
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
}
