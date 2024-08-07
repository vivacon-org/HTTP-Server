package org.vivacon.contract;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public abstract class Action {
    private static final long serialVersionUID = 1L;
    private static final String METHOD_DELETE = "DELETE";
    private static final String METHOD_HEAD = "HEAD";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String METHOD_POST = "POST";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_TRACE = "TRACE";
    private static final String HEADER_IFMODSINCE = "If-Modified-Since";
    private static final String HEADER_LASTMOD = "Last-Modified";
    private static final List<String> SENSITIVE_HTTP_HEADERS = Arrays.asList("authorization", "cookie", "x-forwarded", "forwarded", "proxy-authorization");
    private final transient Object cachedAllowHeaderValueLock = new Object();
    private volatile String cachedAllowHeaderValue = null;
    private volatile boolean cachedUseLegacyDoHead;

    abstract void init(ActionConfig config);

    abstract ActionConfig getServletConfig();

    abstract void destroy();

    protected void doGet(Request req, Response resp) {
    }

    protected long getLastModified(Request req) {
        return -1L;
    }

    protected void doHead(Request req, Response resp) {
    }

    protected void doPost(Request req, Response resp) {

    }

    protected void doPut(Request req, Response resp) {

    }

    protected void doDelete(Request req, Response resp) {

    }

    private void sendMethodNotAllowed(Request req, Response resp, String msg) throws IOException {
        String protocol = req.getProtocol();
        if (!protocol.isEmpty() && !protocol.endsWith("0.9") && !protocol.endsWith("1.0")) {
            resp.sendError(405, msg);
        } else {
            resp.sendError(400, msg);
        }
    }


    private static Method[] getAllDeclaredMethods(Class<?> c) {

        Method[] parentMethods = getAllDeclaredMethods(c.getSuperclass());
        Method[] thisMethods = c.getDeclaredMethods();
        if (parentMethods != null && parentMethods.length > 0) {
            Method[] allMethods = new Method[parentMethods.length + thisMethods.length];
            System.arraycopy(parentMethods, 0, allMethods, 0, parentMethods.length);
            System.arraycopy(thisMethods, 0, allMethods, parentMethods.length, thisMethods.length);
            thisMethods = allMethods;
        }

        return thisMethods;
    }

    private boolean isSensitiveHeader(String headerName) {
        String lcHeaderName = headerName.toLowerCase(Locale.ENGLISH);
        Iterator var3 = SENSITIVE_HTTP_HEADERS.iterator();

        String sensitiveHeaderName;
        do {
            if (!var3.hasNext()) {
                return false;
            }

            sensitiveHeaderName = (String) var3.next();
        } while (!lcHeaderName.startsWith(sensitiveHeaderName));

        return true;
    }

    protected void service(Request req, Response resp) throws IOException {
        String method = req.getMethod();
        long lastModified;
        if (method.equals("GET")) {
            lastModified = this.getLastModified(req);
            if (lastModified == -1L) {
                this.doGet(req, resp);
            } else {
                long ifModifiedSince;
                try {
                    ifModifiedSince = req.getDateHeader("If-Modified-Since");
                } catch (IllegalArgumentException var9) {
                    ifModifiedSince = -1L;
                }

                if (ifModifiedSince < lastModified / 1000L * 1000L) {
                    this.maybeSetLastModified(resp, lastModified);
                    this.doGet(req, resp);
                } else {
                    resp.setStatus(304);
                }
            }
        } else if (method.equals("HEAD")) {
            lastModified = this.getLastModified(req);
            this.maybeSetLastModified(resp, lastModified);
            this.doHead(req, resp);
        } else if (method.equals("POST")) {
            this.doPost(req, resp);
        } else if (method.equals("PUT")) {
            this.doPut(req, resp);
        } else if (method.equals("DELETE")) {
            this.doDelete(req, resp);
        } else if (method.equals("OPTIONS")) {
            // TODO
        } else if (method.equals("TRACE")) {
            // TODO
        } else {
            resp.sendError(501, "errMsg");
        }
    }

    private void maybeSetLastModified(Response resp, long lastModified) {
        if (!resp.containsHeader("Last-Modified")) {
            if (lastModified >= 0L) {
                resp.setHeader("Last-Modified", String.valueOf(lastModified));
            }
        }
    }
}
