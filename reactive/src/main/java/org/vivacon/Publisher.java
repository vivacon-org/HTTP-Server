package org.vivacon;

public interface Publisher<T> {

    void subscribe(Subscriber<? super T> subscriber);
}
