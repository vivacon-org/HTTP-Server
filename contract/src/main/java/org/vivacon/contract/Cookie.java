package org.vivacon.contract;

import java.io.Serializable;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class Cookie implements Serializable {
    private static final long serialVersionUID = 2L;
    private final String name;
    private String value;
    private volatile Map<String, String> attributes;
    private static final String DOMAIN = "Domain";
    private static final String MAX_AGE = "Max-Age";
    private static final String PATH = "Path";
    private static final String SECURE = "Secure";
    private static final String HTTP_ONLY = "HttpOnly";

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setDomain(String pattern) {
        if (pattern == null) {
            this.setAttributeInternal("Domain", (String)null);
        } else {
            this.setAttributeInternal("Domain", pattern.toLowerCase(Locale.ENGLISH));
        }

    }

    public String getDomain() {
        return this.getAttribute("Domain");
    }

    public void setMaxAge(int expiry) {
        this.setAttributeInternal("Max-Age", Integer.toString(expiry));
    }

    public int getMaxAge() {
        String maxAge = this.getAttribute("Max-Age");
        return maxAge == null ? -1 : Integer.parseInt(maxAge);
    }

    public void setPath(String uri) {
        this.setAttributeInternal("Path", uri);
    }

    public String getPath() {
        return this.getAttribute("Path");
    }

    public void setSecure(boolean flag) {
        this.setAttributeInternal("Secure", Boolean.toString(flag));
    }

    public boolean getSecure() {
        return Boolean.parseBoolean(this.getAttribute("Secure"));
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String newValue) {
        this.value = newValue;
    }

    public String getValue() {
        return this.value;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.setAttributeInternal("HttpOnly", Boolean.toString(httpOnly));
    }

    public boolean isHttpOnly() {
        return Boolean.parseBoolean(this.getAttribute("HttpOnly"));
    }

    public void setAttribute(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("ABC");
        } else {
            if (name.equalsIgnoreCase("Max-Age")) {
                if (value == null) {
                    this.setAttributeInternal("Max-Age", (String)null);
                } else {
                    this.setMaxAge(Integer.parseInt(value));
                }
            } else {
                this.setAttributeInternal(name, value);
            }

        }
    }

    private void setAttributeInternal(String name, String value) {
        if (this.attributes == null) {
            if (value == null) {
                return;
            }

            this.attributes = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        }

        if (value == null) {
            this.attributes.remove(name);
        } else {
            this.attributes.put(name, value);
        }

    }

    public String getAttribute(String name) {
        return this.attributes == null ? null : (String)this.attributes.get(name);
    }

    public Map<String, String> getAttributes() {
        return this.attributes == null ? Collections.emptyMap() : Collections.unmodifiableMap(this.attributes);
    }
}
