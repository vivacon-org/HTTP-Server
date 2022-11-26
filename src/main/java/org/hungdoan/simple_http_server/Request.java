package org.hungdoan.simple_http_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class Request {

    private static final Logger LOG = LoggerFactory.getLogger(Request.class);
    private Socket client;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String path;
    private String verb;
    private String fullUrl;
    private String httpVersion;

    public Request(Socket client) {
        this.client = client;
        try {
            parse();
        } catch (IOException e) {
            LOG.info("Can not parse the malformed request");
        }
    }

    private boolean parse() throws IOException {
        InputStream inputStream = client.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String initialLine = bufferedReader.readLine();
        StringTokenizer tokenizer = new StringTokenizer(initialLine);
        LOG.info("Initial line of the request : " + initialLine);
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

        //consume the header
        while (true) {
            String headerLine = bufferedReader.readLine();
            LOG.info("Header line of the request : " + initialLine);
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

        // consume verb and http version
        verb = components[0];
        httpVersion = components[2].substring(components[2].indexOf("/"));
        bufferedReader.close();

        LOG.info("Request info: " + this);
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
                ", fullUrl='" + fullUrl + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                '}';
    }
}
