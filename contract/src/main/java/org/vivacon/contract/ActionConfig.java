package org.vivacon.contract;

import java.util.Enumeration;

public interface ActionConfig {

    String getActionName();

    Context getContext();

    String getInitParameter(String var1);

    Enumeration<String> getInitParameterNames();
}
