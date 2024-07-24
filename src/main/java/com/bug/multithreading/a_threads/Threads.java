package com.bug.multithreading.a_threads;

import com.bug.multithreading.shared.Tasks;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.concurrent.*;

/**
 * Basic {@linkplain Thread} and {@linkplain Callable}
 */
@Slf4j
public class Threads {
    public static void main(String[] args) {
        // For best results, run these one at a time. It's pretty hard to follow the output otherwise
        basicThreadAndCallable();
        properCallable();
        andThen();
        basicExecutor();
        moreUsefulExecutor();
        virtualThreads();
        virtualThreadsSurprise();
    }

    @SneakyThrows
    private static void basicThreadAndCallable() {
        new Thread(Tasks::pretendHardWork).start();
        log.info("Work requested");

        var result = ((Callable<Double>) Tasks::pretendHardWorkWithAResult).call();
        log.info("Calculation requested");
        log.info("And the winner is: {}", result);
        // Note that we do not have to wait for the threads to finish first. Why? Because the JVM will not shut down
        // before all non-daemon threads have finished
    }

    @SneakyThrows
    private static void properCallable() {
        try (var executor = Executors.newSingleThreadExecutor()) {
            var task = ((Callable<Double>) Tasks::pretendHardWorkWithAResult);
            var futureResult = executor.submit(task);
            log.info("Calculation requested");
            log.info("And the winner is: {}", futureResult.get(2000, TimeUnit.MILLISECONDS));
        }
    }

    private static void andThen() {
        var task = new TaskWithFollowUp<>(
                Tasks::pretendHardWorkWithAResult,
                result -> log.info("And the winner is: {}", result)) {
        };
        try (var executor = Executors.newSingleThreadExecutor()) {
            executor.submit(task);
            log.info("Work requested");
        }
    }

    /**
     * Watch in VisualVM
     */
    @SneakyThrows
    private static void basicExecutor() {
        try (var exSvc = Executors.newFixedThreadPool(20)) {
            for (int i = 0; i < 50; i++) {
                exSvc.submit(Tasks::pretendHardWork);
            }
        }
    }

    private static void moreUsefulExecutor() {
        try (var exSvc = Executors.newFixedThreadPool(20, Thread.ofPlatform().name("Important stuff-", 1).factory())) {
            for (int i = 0; i < 50; i++) {
                exSvc.submit(Tasks::pretendHardWork);
            }
        }
    }

    /**
     * Watch in VisualVM
     */
    private static void virtualThreads() {
        var threads = new HashSet<Future<?>>();
        try (var exSvc = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5_000_000; i++) {
                var t = exSvc.submit(Tasks::pretendHardWork);
                threads.add(t);
            }
            // This time we DO have to wait. The threads this executor uses are daemon threads, JVM will not wait for
            // them to finish
            threads.parallelStream().forEach(t -> {
                try {
                    t.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void virtualThreadsSurprise() {
        var tBuilder = Thread.ofVirtual().name("Important virtual stuff-", 1).factory();
        var threads = new HashSet<Thread>();
        for (int i = 0; i < 5_000_000; i++) {
            var t = tBuilder.newThread(Tasks::numberCrunching);
            t.start();
            threads.add(t);
        }
        threads.parallelStream().forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
