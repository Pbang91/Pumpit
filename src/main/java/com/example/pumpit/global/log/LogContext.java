package com.example.pumpit.global.log;

import org.slf4j.MDC;

import java.util.Map;

public class LogContext {
    private static final String TRACE_ID_KEY = "traceId";
    private static final String USER_ID_KEY = "userId";

    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static void setUserId(String userId) {
        MDC.put(USER_ID_KEY, userId);
    }

    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    public static String getUserId() {
        return MDC.get(USER_ID_KEY);
    }

    public static Map<String, String> getContextMap() {
        return MDC.getCopyOfContextMap();
    }

    public static void setContextMap(Map<String, String> contextMap) {
        MDC.setContextMap(contextMap);
    }

    public static void clear() {
        MDC.clear();
    }
}
