package com.bug.multithreading.c_extras;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContextPreview {
    public static final ScopedValue<SessionInfo> SESSION = ScopedValue.newInstance();
}
