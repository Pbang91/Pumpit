package com.example.pumpit.global.filter;

import com.example.pumpit.global.log.*;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class LoggingFilter extends OncePerRequestFilter {
    private final Logger infoLogger = LoggerFactory.getLogger("INFO_LOGGER");
    private final Logger errorLogger = LoggerFactory.getLogger("ERROR_LOGGER");
    private final Logger warnLogger = LoggerFactory.getLogger("WARN_LOGGER");

    private final ObjectMapper objectMapper;

    public LoggingFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), false);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (isSwaggerRequest(uri)) {
            chain.doFilter(request, response);
            return;
        }

        CustomRequestWrapper wrappedRequest = new CustomRequestWrapper(request);
        CustomResponseWrapper wrappedResponse = new CustomResponseWrapper(response);

        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        LogContext.setTraceId(traceId);
        LogContext.setUserId("ANONYMOUS");

        try {
            logRequest(wrappedRequest);
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logResponse(wrappedResponse);
            LogContext.clear();
            wrappedResponse.flushBuffer();
        }
    }

    private void logRequest(CustomRequestWrapper request) {
        try {
            Map<String, Object> requestBodyJson = null;
            Map<String, Object> requestQueryJson = null;

            if (request.getCachedBodyAsString() == null && request.getQueryString() != null) {
                requestQueryJson = objectMapper.readValue(request.getQueryString(), new TypeReference<>() {});
            } else {
                boolean isJsonBody = request.getContentType() != null
                        && request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE);

                if (!isJsonBody) {
                    requestBodyJson =  new HashMap<>() {
                        {
                            put("size", request.getContentLength());
                        }
                    };
                } else {
                    requestBodyJson = objectMapper.readValue(request.getCachedBodyAsString(), new TypeReference<>() {});
                }
            }

            RequestLog requestLog = new RequestLog(
                    "REQUEST",
                    LogContext.getTraceId(),
                    System.currentTimeMillis(),
                    request.getRemoteAddr(),
                    request.getMethod(),
                    request.getRequestURI(),
                    requestBodyJson,
                    requestQueryJson,
                    LogContext.getUserId()
            );

            infoLogger.info(objectMapper.writeValueAsString(requestLog));
        } catch (Exception e) {
            warnLogger.warn("request 로그 실패. 사유: ", e);
        }
    }

    private void logResponse(CustomResponseWrapper response) {
        try {
            String responseBody = new String(response.getCachedBody(), StandardCharsets.UTF_8);
            Map<String, Object> responseJson = objectMapper.readValue(responseBody, new TypeReference<>() {
            });

            ResponseLog responseLog = new ResponseLog(
                    "RESPONSE",
                    LogContext.getTraceId(),
                    System.currentTimeMillis(),
                    response.getStatus(),
                    responseJson
            );

            infoLogger.info(objectMapper.writeValueAsString(responseLog));
        } catch (Exception e) {
            warnLogger.warn("response 로그 실패. 사유: ", e);
        }
    }

    private boolean isSwaggerRequest(String uri) {
        return uri.contains("/swagger") || uri.contains("/api-docs");
    }
}
