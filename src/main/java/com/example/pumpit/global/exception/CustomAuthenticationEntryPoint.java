package com.example.pumpit.global.exception;

import com.example.pumpit.global.exception.enums.CustomExceptionData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                  "code": "%s",
                  "description": "%s",
                  "details": "%s"
                }
                """.formatted(
                    CustomExceptionData.JWT_TOKEN_INVALID.getCode(),
                    CustomExceptionData.JWT_TOKEN_INVALID.getDescription(),
                    authException.getMessage())
        );
    }
}
