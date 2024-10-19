package org.vivacon;

public class ArrayPublisher<T> implements Publisher<T> {


    private final T[] source;

    public ArrayPublisher(T[] source) {
        this.source = source;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        
        subscriber.onSubscribe(new Subscription() {

            int index;

            @Override
            public void request(long backpressure) {
                try {
                    for (int countingEachRequest = 0;
                         countingEachRequest < backpressure && index < source.length;
                         countingEachRequest++, index++) {

                        subscriber.onNext(source[index]);
                    }
                    subscriber.onComplete();
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }

            @Override
            public void cancel() {

            }
        });
    }
}
