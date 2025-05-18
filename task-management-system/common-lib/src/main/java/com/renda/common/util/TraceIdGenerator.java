package com.renda.common.util;

import java.util.UUID;

public class TraceIdGenerator {

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    public static String generateTraceId() {
        String traceId = UUID.randomUUID().toString();
        TRACE_ID.set(traceId);
        return traceId;
    }

    public static String getCurrentTraceId() {
        return TRACE_ID.get();
    }

    public static void clear() {
        TRACE_ID.remove();
    }
}
