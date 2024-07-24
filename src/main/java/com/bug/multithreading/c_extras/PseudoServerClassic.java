package com.bug.multithreading.c_extras;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
public class PseudoServerClassic {
    public void doRequestWork() {
        var session = ContextClassic.getSession();
        log.info("I was called by user {} having the role of {}", session.user(), session.role());
    }
}
