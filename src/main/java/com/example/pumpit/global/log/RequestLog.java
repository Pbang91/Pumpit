package com.example.pumpit.global.log;

public record RequestLog(
        String traceId,
        long timestamp,
        String ip,
        String method,
        String uri,
        String body,
        String userId
) {
}
