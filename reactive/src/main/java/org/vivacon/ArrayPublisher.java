package org.vivacon;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ArrayPublisher<T> implements Publisher<T> {

    private final T[] source;

    public ArrayPublisher(T[] source) {
        this.source = source;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new Subscription() {

            AtomicInteger index = new AtomicInteger(0);
            AtomicLong requested = new AtomicLong(0);

            @Override
            public void request(long n) {
                if (n <= 0) {
                    subscriber.onError(new IllegalArgumentException("Request must be greater than 0"));
                    return;
                }

                long previousRequested = requested.getAndAccumulate(n, Long::sum);
                if (previousRequested == 0) {
                    emitItems();
                }
            }

            private void emitItems() {
                try {
                    while (requested.get() > 0) {

                        int currentIndex = index.getAndIncrement();
                        
                        if (currentIndex >= source.length) {
                            subscriber.onComplete();
                            return;
                        }

                        T element = source[currentIndex];
                        if (element == null) {
                            subscriber.onError(new NullPointerException());
                            return;
                        }

                        subscriber.onNext(element);

                        requested.decrementAndGet();
                    }
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }

            @Override
            public void cancel() {
                requested.set(0);
            }
        });
    }
}
