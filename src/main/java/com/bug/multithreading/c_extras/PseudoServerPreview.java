package com.bug.multithreading.c_extras;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PseudoServerPreview {
    public void doRequestWork() {
        log.info("I was called by user {} having the role of {}", ContextPreview.SESSION.get().user(), ContextPreview.SESSION.get().role());
    }
}
