package org.vivacon;

public interface Subscription {
    void request(long t);

    void cancel();
}
