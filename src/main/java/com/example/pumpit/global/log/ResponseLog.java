package com.example.pumpit.global.log;

public record ResponseLog(
        String logType,
        String traceId,
        long timestamp,
        int status,
        Object body
) {
}
