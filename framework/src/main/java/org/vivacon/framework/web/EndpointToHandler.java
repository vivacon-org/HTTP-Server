package org.vivacon.framework.web;

import java.lang.reflect.Method;

public interface EndpointToHandler {

    Method getEndpointHandler(String path);

    Object getController(String path);
}
