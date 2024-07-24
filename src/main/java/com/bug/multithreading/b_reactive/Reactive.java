package com.bug.multithreading.b_reactive;

import com.bug.multithreading.shared.Tasks;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Emitter;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
public class Reactive {
    private static final int STREAM_ITEMS = 20;

    public static void main(String[] args) {
        //simpleProcessing();
        aBitMoreUsefulProcessing();
    }

    @SneakyThrows
    private static void simpleProcessing() {
        var d = Flowable.create((FlowableOnSubscribe<Double>) emitter -> {
                    for (var i = 0; i < STREAM_ITEMS; i++) {
                        emitter.onNext(Tasks.pretendHardWorkWithAResult());
                    }
                    emitter.onComplete();
                }, BackpressureStrategy.DROP)
                .doOnComplete(() -> log.info("All done"))
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .subscribe(result -> log.info("Got {}", result));
        Thread.sleep(25_000);
        d.dispose();
    }

    @SneakyThrows
    private static void aBitMoreUsefulProcessing() {
        var numbers = newFlowable(Tasks::pretendHardWorkWithAResult);
        var uuids = newFlowable(Tasks::pretendHardWorkWithAnotherResult);

        var d = numbers
                .zipWith(uuids, Pair::new)
                .observeOn(Schedulers.newThread())
                .subscribe(p -> log.info("{}", p));

        Thread.sleep(25_000);
        d.dispose();
    }

    private static <T> Flowable<T> newFlowable(Supplier<T> s) {
        return Flowable.generate(
                        () -> 0,
                        (Integer state, Emitter<T> emitter) -> {
                            if (state == STREAM_ITEMS) {
                                emitter.onComplete();
                            }
                            emitter.onNext(s.get());
                            return ++state;
                        }
                )
                .subscribeOn(Schedulers.computation());
    }

    private record Pair(double d, UUID uuid) {
    }
}
