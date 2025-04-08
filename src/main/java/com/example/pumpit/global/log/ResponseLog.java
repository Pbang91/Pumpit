package com.example.pumpit.global.log;

public record ResponseLog(
        String traceId,
        long timestamp,
        String uri,
        int status,
        Object body,
        long duration
) {
}
