package org.vivacon;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.LongStream;

public class ArrayPublisherTest extends PublisherVerification<Long> {

    public ArrayPublisherTest() {
        super(new TestEnvironment());
    }

    @Override
    public Publisher<Long> createPublisher(long l) {
        return new ArrayPublisher<>(generate(l));
    }

    @Override
    public Publisher<Long> createFailedPublisher() {
        return null;
    }

    @Test
    public void everyMethodInSubscriberShouldBeExecutedInParticularOrder() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<String> observedSignals = new ArrayList<>();
        ArrayPublisher<Long> arrayPublisher = new ArrayPublisher<>(generate(5));

        arrayPublisher.subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                observedSignals.add("onSubscribe");
                s.request(10);
            }

            @Override
            public void onNext(Long aLong) {
                observedSignals.add("onNext(" + aLong + ")");
            }

            @Override
            public void onError(Throwable t) {
                observedSignals.add("onError");
            }

            @Override
            public void onComplete() {
                observedSignals.add("onComplete");
                latch.countDown();
            }
        });

        Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));

        List<String> expectedCallingFunctions = new ArrayList<>();
        expectedCallingFunctions.add("onSubscribe");
        for (int i = 0; i < 5; i++) {
            expectedCallingFunctions.add("onNext(" + i + ")");
        }
        expectedCallingFunctions.add("onComplete");

        int index = 0;
        for (String signal : observedSignals) {
            if (!expectedCallingFunctions.get(index).equals(signal)) {
                throw new RuntimeException();
            }
            index++;
        }
    }

    @Test
    public void mustSupportBackpressureControl() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Long> collected = new ArrayList<>();
        long toRequest = 5L;
        Long[] array = generate(toRequest);

        ArrayPublisher<Long> publisher = new ArrayPublisher<>(array);
        Subscription[] subscriptions = new Subscription[1];
        publisher.subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                subscriptions[0] = s;
            }

            @Override
            public void onNext(Long aLong) {
                collected.add(aLong);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });

        Assert.assertTrue(collected.isEmpty());

        subscriptions[0].request(1);
        org.assertj.core.api.Assertions.assertThat(collected).containsExactly(0L);

        subscriptions[0].request(2);
        org.assertj.core.api.Assertions.assertThat(collected).containsExactly(0L, 1L, 2L);

        subscriptions[0].request(20);
        org.assertj.core.api.Assertions.assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
        org.assertj.core.api.Assertions.assertThat(collected).containsExactly(array);
    }


    @Test
    public void mustSendNPENormally() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Long[] array = new Long[]{null};
        AtomicReference<Throwable> error = new AtomicReference<>();
        ArrayPublisher<Long> publisher = new ArrayPublisher<>(array);

        publisher.subscribe(new Subscriber<Long>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(4);
            }

            @Override
            public void onNext(Long aLong) {

            }

            @Override
            public void onError(Throwable t) {
                error.set(t);
                latch.countDown();
            }

            @Override
            public void onComplete() {

            }
        });

        latch.await(1, TimeUnit.SECONDS);
        org.assertj.core.api.Assertions.assertThat(error.get()).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldNotDieInStackOverflow() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Long> collected = new ArrayList<>();
        long toRequest = 1000L;
        Long[] array = generate(toRequest);

        ArrayPublisher<Long> publisher = new ArrayPublisher<>(array);

        publisher.subscribe(new Subscriber<>() {

            Subscription s;

            @Override
            public void onSubscribe(Subscription s) {
                this.s = s;
                s.request(1);
            }

            @Override
            public void onNext(Long aLong) {
                collected.add(aLong);
                s.request(1);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });

        org.assertj.core.api.Assertions.assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();

        org.assertj.core.api.Assertions.assertThat(collected).containsExactly(array);
    }

    @Test
    public void shouldBePossibleToCancelSubscription() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        ArrayList<Long> collected = new ArrayList<>();
        long toRequest = 1000L;
        Long[] arr = generate(toRequest);
        ArrayPublisher<Long> publisher = new ArrayPublisher<>(arr);
        publisher.subscribe(new Subscriber<Long>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.cancel();
                s.request(toRequest);
            }

            @Override
            public void onNext(Long aLong) {
                collected.add(aLong);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {
                latch.countDown();
            }
        });

        org.assertj.core.api.Assertions.assertThat(latch.await(5, TimeUnit.SECONDS)).isFalse();
        org.assertj.core.api.Assertions.assertThat(collected).isEmpty();
    }

    static Long[] generate(long num) {
        return LongStream.range(0, num >= Integer.MAX_VALUE ? 1000000 : num)
                .boxed()
                .toArray(Long[]::new);
    }
}
