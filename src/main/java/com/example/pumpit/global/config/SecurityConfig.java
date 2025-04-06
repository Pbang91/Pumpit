package com.example.pumpit.global.config;

import com.example.pumpit.global.filter.JwtAutFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtAutFilter jwtAutFilter;

    public SecurityConfig(JwtAutFilter jwtAutFilter) {
        this.jwtAutFilter = jwtAutFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers(
                                                "/swagger-ui/**",
                                                "/swagger-resource/**",
                                                "/v3/api-docs/**"
                                        )
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/api/v1/user",
                                                "/api/v1/user/login"
                                        ).permitAll()
                                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAutFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
