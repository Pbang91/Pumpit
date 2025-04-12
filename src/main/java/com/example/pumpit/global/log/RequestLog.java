package com.example.pumpit.global.log;

public record RequestLog(
        String logType,
        String traceId,
        long timestamp,
        String ip,
        String method,
        String uri,
        Object body,
        Object queryParams,
        String userId
) {
}
