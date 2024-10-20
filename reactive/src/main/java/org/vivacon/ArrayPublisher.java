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
            public void request(long newRequested) {
                if (newRequested <= 0 && !cancelled.get()) {
                    cancel();
                    subscriber.onError(new IllegalArgumentException("Request must be greater than 0, but it was " + newRequested));
                    return;
                }

                long previousRequested;
                do {
                    previousRequested = requested.get();

                    if (previousRequested == Long.MAX_VALUE) {
                        return;
                    }

                    newRequested = previousRequested + newRequested;

                    if (newRequested <= 0) {
                        newRequested = Long.MAX_VALUE;
                    }
                } while (!requested.compareAndSet(previousRequested, newRequested));

                if (previousRequested == 0) {
                    try {
                        emitItems();
                    } catch (Exception ex) {
                        subscriber.onError(ex);
                    }
                }
            }

            private void emitItems() {

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
            }

            @Override
            public void cancel() {
                requested.set(0);
                cancelled.set(true);
            }
        });
    }
}
