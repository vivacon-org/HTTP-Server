package org.vivacon.contract;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Set;

public interface Context {

    String getContextPath();

    int getMajorVersion();

    int getMinorVersion();

    int getEffectiveMajorVersion();

    int getEffectiveMinorVersion();

    String getMimeType(String var1);

    Set<String> getResourcePaths(String var1);

    URL getResource(String var1) throws MalformedURLException;

    InputStream getResourceAsStream(String var1);

    void log(String var1);

    void log(String var1, Throwable var2);

    String getRealPath(String var1);

    String getServerInfo();

    String getInitParameter(String var1);

    Enumeration<String> getInitParameterNames();

    boolean setInitParameter(String var1, String var2);

    Object getAttribute(String var1);

    Enumeration<String> getAttributeNames();

    void setAttribute(String var1, Object var2);

    void removeAttribute(String var1);

    String getServletContextName();

    void addListener(String var1);

    <T extends EventListener> void addListener(T var1);

    void addListener(Class<? extends EventListener> var1);

    ClassLoader getClassLoader();

    void declareRoles(String... var1);

    String getVirtualServerName();

    int getSessionTimeout();

    void setSessionTimeout(int var1);

    String getRequestCharacterEncoding();

    void setRequestCharacterEncoding(String var1);

    String getResponseCharacterEncoding();

    void setResponseCharacterEncoding(String var1);
}
