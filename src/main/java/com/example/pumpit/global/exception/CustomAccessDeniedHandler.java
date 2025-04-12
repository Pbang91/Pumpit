package com.example.pumpit.global.exception;

import com.example.pumpit.global.exception.enums.CustomExceptionData;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                    "code": "%s",
                    "description": "%s",
                    "details": "%s"
                }
                """.formatted(
                CustomExceptionData.USER_FORBIDDEN.getCode(),
                CustomExceptionData.USER_FORBIDDEN.getDescription(),
                accessDeniedException.getMessage()
        ));
    }
}
