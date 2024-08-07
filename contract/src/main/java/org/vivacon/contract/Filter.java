package org.vivacon.contract;

import java.io.IOException;

public interface Filter {

    void doFilter(Request request, Response response, FilterChain chain);

    void destroy();
}
