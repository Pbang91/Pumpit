package com.example.pumpit.global.filter;

import com.example.pumpit.global.log.*;
import com.example.pumpit.global.util.CustomUserDetails;
import com.example.pumpit.global.util.JwtService;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class CustomLogFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger("REQUEST_RESPONSE_LOGGER");
    private final ObjectMapper objectMapper;
    private final JwtService jwtService;

    public CustomLogFilter(ObjectMapper objectMapper, JwtService jwtService) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), false);
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        if (isSwaggerRequest(uri)) {
            chain.doFilter(request, response);
            return;
        }

        CustomRequestWrapper wrappedRequest = new CustomRequestWrapper(httpRequest);
        CustomResponseWrapper wrappedResponse = new CustomResponseWrapper(httpResponse);

        long start = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        String userId = extractUserId(httpRequest);

        LogContext.set(traceId, userId);

        try {
            logRequest(wrappedRequest);
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - start;
            logResponse(wrappedRequest, wrappedResponse, duration);
            wrappedResponse.copyBodyToResponse(); // 꼭 필요: 실제 응답 본문 전송
            LogContext.clear();
        }
    }

    private void logRequest(CustomRequestWrapper request) {
        try {
            String body = request.getCachedBodyAsString();

            RequestLog requestLog = new RequestLog(
                    LogContext.getTraceId(),
                    System.currentTimeMillis(),
                    request.getRemoteAddr(),
                    request.getMethod(),
                    request.getRequestURI(),
                    body,
                    LogContext.getUserId()
            );

            logger.info(objectMapper.writeValueAsString(requestLog));
        } catch (Exception e) {
            logger.warn("request 로그 실패. 사유: ", e);
        }
    }

    private void logResponse(CustomRequestWrapper request, CustomResponseWrapper response, long duration) throws IOException {
        try {
            String responseBody = new String(response.getCachedBody(), StandardCharsets.UTF_8);
            Map<String, Object> responseJson = objectMapper.readValue(responseBody, new TypeReference<>() {});

            ResponseLog responseLog = new ResponseLog(
                    LogContext.getTraceId(),
                    System.currentTimeMillis(),
                    request.getRequestURI(),
                    response.getStatus(),
                    responseJson,
                    duration
            );

            logger.info(objectMapper.writeValueAsString(responseLog));
        } catch (Exception e) {
            logger.warn("response 로그 실패. 사유: ", e);
        }
    }

    private String extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Authentication auth = jwtService.getAuthentication(token);
                Object principal = auth.getPrincipal();

                if (principal instanceof CustomUserDetails userDetails) {
                    return String.valueOf(userDetails.getId());
                }

                return principal.toString(); // fallback
            } catch (Exception e) {
                return "INVALID_TOKEN";
            }
        }
        return "ANONYMOUS";
    }

    private boolean isSwaggerRequest(String uri) {
        return uri.contains("/swagger") || uri.contains("/api-docs");
    }
}
