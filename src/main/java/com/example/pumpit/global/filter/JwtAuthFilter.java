package com.example.pumpit.global.filter;

import com.example.pumpit.global.exception.enums.CustomExceptionData;
import com.example.pumpit.global.util.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    private String extractJwt(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return null;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String token = extractJwt(request);

        if (token != null) {
            try {
                Authentication authentication = jwtService.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");

                String jsonBody = """
                        {
                            "code": "%s"
                            "description": "%s",
                            "details": "%s"
                        }
                        """.formatted(
                                CustomExceptionData.JWT_TOKEN_INVALID.getCode(),
                                CustomExceptionData.JWT_TOKEN_INVALID.getDescription(),
                                e.getMessage()
                );

                response.getWriter().write(jsonBody);

                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
