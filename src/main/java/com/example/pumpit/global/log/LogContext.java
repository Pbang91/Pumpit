package com.example.pumpit.global.log;

import lombok.Getter;
import org.slf4j.MDC;

public class LogContext {
    private static final String TRACE_ID_KEY = "traceId";
    private static final String USER_ID_KEY = "userId";

    public static void set(String traceId, String userId) {
        MDC.put(TRACE_ID_KEY, traceId);
        MDC.put(USER_ID_KEY, userId);
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    public static String getUserId() {
        return MDC.get(USER_ID_KEY);
    }

    public static void clear() {
        MDC.clear();
    }
}
