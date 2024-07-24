package com.bug.multithreading.a_threads;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public abstract class TaskWithFollowUp<T> implements Runnable {
    private final Callable<T> callable;
    private final Callback<T> callback;

    @SneakyThrows
    public void run() {
        T workResult = callable.call();
        callback.callback(workResult);
    }
}
