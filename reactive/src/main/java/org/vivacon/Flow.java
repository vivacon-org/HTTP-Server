package org.vivacon;

import org.reactivestreams.Publisher;

import java.util.function.Function;

public abstract class Flow<T> implements Publisher<T> {

    public static <T> Flow<T> just(T... elements) {
        return fromArray(elements);
    }

    public static <T> Flow<T> fromArray(T[] array) {
        return new ArrayPublisher<T>(array);
    }

    public <R> Flow<R> map(Function<T, R> mapper) {
        return new MapPublisher<T, R>(this, mapper);
    }
}