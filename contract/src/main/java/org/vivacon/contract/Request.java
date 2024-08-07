package org.vivacon.contract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public interface Request {

    Object getAttribute(String key);

    Enumeration<String> getAttributeNames();

    String getCharacterEncoding();

    void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException;

    int getContentLength();

    long getContentLengthLong();

    String getContentType();

    String getParameter(String key);

    Enumeration<String> getParameterNames();

    String[] getParameterValues(String key);

    Map<String, String[]> getParameterMap();

    String getProtocol();

    String getScheme();

    String getServerName();

    int getServerPort();

    BufferedReader getReader() throws IOException;

    String getRemoteAddr();

    String getRemoteHost();

    void setAttribute(String key, Object value);

    void removeAttribute(String key);

    Locale getLocale();

    Enumeration<Locale> getLocales();

    boolean isSecure();

    int getRemotePort();

    String getLocalName();

    String getLocalAddr();

    int getLocalPort();

    String getRequestId();

    String getProtocolRequestId();
    InputStream getInputStream() throws IOException;

    String getMethod();

    long getDateHeader(String s);
}
