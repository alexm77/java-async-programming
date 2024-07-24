package com.bug.multithreading.c_extras;

public class ContextClassic {
    private static final ThreadLocal<SessionInfo> session = new ThreadLocal<>();

    public static void setSession(SessionInfo info) {
        ContextClassic.session.set(info);
    }

    public static SessionInfo getSession() {
        return session.get();
    }
}
