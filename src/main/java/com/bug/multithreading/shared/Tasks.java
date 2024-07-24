package com.bug.multithreading.shared;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public final class Tasks {
    @SneakyThrows
    public static void pretendHardWork() {
        Thread.sleep(60 * 1_000);
        log.info("I'm working");
    }

    public static void numberCrunching() {
        var result = 1.0;
        for (int i = 0; i < 10_000_000; i++) {
            result *= Math.pow(Math.sin(2 * Math.PI * Math.random()), 2);
        }
        log.info("Got result: {}", result);
    }

    @SneakyThrows
    public static Double pretendHardWorkWithAResult() {
        Thread.sleep(1_000);
        log.info("I'm computing");
        return Math.random();
    }

    @SneakyThrows
    public static UUID pretendHardWorkWithAnotherResult() {
        Thread.sleep(1_000);
        log.info("I'm also computing");
        return UUID.randomUUID();
    }

    public static Stream<Double> streamOfHardWork() {
        var builder = Stream.<Double>builder();
        for (var i = 0; i < 16; i++) {
            builder.accept(pretendHardWorkWithAResult());
        }
        return builder.build();
    }

    public static Stream<UUID> streamOfOtherHardWork() {
        var builder = Stream.<UUID>builder();
        for (var i = 0; i < 16; i++) {
            builder.accept(pretendHardWorkWithAnotherResult());
        }
        return builder.build();
    }
}
