package org.vivacon.contract;

import java.util.Enumeration;

public interface Session {

    long getCreationTime();

    String getId();

    long getLastAccessedTime();

    Context getServletContext();

    void setMaxInactiveInterval(int time);

    int getMaxInactiveInterval();

    Object getAttribute(String key);

    Enumeration<String> getAttributeNames();

    void setAttribute(String key, Object value);

    void removeAttribute(String key);

    void invalidate();

    boolean isNew();
}
