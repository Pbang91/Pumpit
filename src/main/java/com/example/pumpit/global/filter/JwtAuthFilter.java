package com.example.pumpit.global.filter;

import com.example.pumpit.global.exception.JwtAuthenticationException;
import com.example.pumpit.global.log.LogContext;
import com.example.pumpit.global.util.CustomUserDetails;
import com.example.pumpit.global.util.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
                String userId = ((CustomUserDetails) authentication.getPrincipal()).getId().toString();

                LogContext.setUserId(userId);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                throw new JwtAuthenticationException("유효하지 않은 JWT", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
