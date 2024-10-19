package org.vivacon;


@FunctionalInterface
public interface Publisher<T> {

    void subscribe(Subscriber<? super T> subscriber);
}
