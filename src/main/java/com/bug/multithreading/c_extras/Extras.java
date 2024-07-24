package com.bug.multithreading.c_extras;

import com.bug.multithreading.shared.Tasks;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.concurrent.*;

@Slf4j
public class Extras {
    public static void main(String[] args) {
//        structuredConcurrency();
//        pseudoSessionClassic();
        pseudoSessionPreview();
//        pseudoSessionClassicV2();
//        pseudoSessionPreviewV2();

    }

    @SneakyThrows
    private static void structuredConcurrency() {
        try (final var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var subtask1 = scope.fork(Tasks::pretendHardWorkWithAResult);
            var subtask2 = scope.fork(Tasks::pretendHardWorkWithAnotherResult);
            scope.join();
            log.info("Task1 says {}, task2 says {}", subtask1.get(), subtask2.get());
        }
    }

    private static void pseudoSessionClassic() {
        var requestsCount = 100;
        var server = new PseudoServerClassic();
        try (var exSvc = Executors.newFixedThreadPool(16)) {
            for (int i = 0; i < requestsCount; i++) {
                var finalI = i;
                exSvc.submit(() -> {
                    ContextClassic.setSession(new SessionInfo(String.format("user-%03d", finalI), String.format("role-%03d", finalI)));
                    server.doRequestWork();
                });
            }
        }
        // Glaring issue: we have set the ThreadLocal value, but we forgot to clear it. That's an automatic memory leak
    }

    private static void pseudoSessionPreview() {
        var requestsCount = 100;
        try (var exSvc = Executors.newFixedThreadPool(16)) {
            for (int i = 0; i < requestsCount; i++) {
                var finalI = i;
                exSvc.submit(() -> ScopedValue
                        .where(ContextPreview.SESSION, new SessionInfo(String.format("user-%03d", finalI), String.format("role-%03d", finalI)))
                        .run(() -> log.info("I was called by user {} having the role of {}", ContextPreview.SESSION.get().user(), ContextPreview.SESSION.get().role())));
            }
        }
        // Scoped values are cleaned up automatically, when they go out of scope (go figure)
    }

}
