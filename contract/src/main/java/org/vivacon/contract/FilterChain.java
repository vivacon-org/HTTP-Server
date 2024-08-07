package org.vivacon.contract;

public interface FilterChain {

    void doFilter(Request request, Response response, FilterChain chain);
}
