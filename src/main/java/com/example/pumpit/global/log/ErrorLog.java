package com.example.pumpit.global.log;

public record ErrorLog(
        String traceId,
        long timestamp,
        String uri,
        String message,
        String errorStackTrace,
        String userId
) {
}
