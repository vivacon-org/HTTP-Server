package org.vivacon;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ArrayPublisher<T> implements Publisher<T> {

    private final T[] source;

    public ArrayPublisher(T[] source) {
        this.source = source;
    }

    @Override
    public void subscribe(org.reactivestreams.Subscriber<? super T> subscriber) {
        subscriber.onSubscribe(new Subscription() {

            AtomicInteger index = new AtomicInteger(0);
            AtomicLong requested = new AtomicLong(0);
            AtomicBoolean cancelled = new AtomicBoolean(false);

            @Override
            public void request(long n) {
                if (n <= 0 && !cancelled.get()) {
                    cancel();
                    subscriber.onError(new IllegalArgumentException("Request must be greater than 0, but it was " + n));
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

                        if (cancelled.get()) {
                            return;
                        }

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
                cancelled.set(true);
            }
        });
    }
}
