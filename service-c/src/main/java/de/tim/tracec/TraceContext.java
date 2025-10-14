package de.tim.tracec;

import java.util.UUID;

public class TraceContext {

    private static ThreadLocal<String> traceId = new ThreadLocal<>();
    private static ThreadLocal<String> currentSpanId = new ThreadLocal<>();

    public static void createTraceId() {
        if (traceId.get() != null) {
            return;
        }
        traceId.set(UUID.randomUUID().toString());
    }

    public static void setTraceId(String traceIdFromHeader) {
        if (traceIdFromHeader == null || traceIdFromHeader.isBlank()) {
            return;
        }
        traceId.set(traceIdFromHeader);
    }

    public static String getCurrentSpanId() {
        return currentSpanId.get();
    }

    public static void setCurrentSpanId(String spanId) {
        if (spanId == null || spanId.isBlank()) {
            return;
        }
        currentSpanId.set(spanId);
    }

    public static String getTraceId() {
        return traceId.get();
    }


    public static void clearAll() {
        traceId.remove();
        currentSpanId.remove();
    }
}
