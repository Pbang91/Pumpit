package com.example.pumpit.global.config;

import com.example.pumpit.global.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JwtAuthFilter jwtAutFilter;

    public SecurityConfig(JwtAuthFilter jwtAutFilter) {
        this.jwtAutFilter = jwtAutFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();

                    // TODO: cors 설정 해주기
                    config.addAllowedOriginPattern("*"); // 모든 Origin 허용
                    config.addAllowedHeader("*");        // 모든 헤더 허용
                    config.addAllowedMethod("*");        // 모든 HTTP 메서드 허용
                    config.setAllowCredentials(false);   // true 시 Origin에 * 못 씀

                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers(
                                                "/swagger-ui/**",
                                                "/api-docs/**"
                                        )
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.POST,
                                                "/api/v1/user/email/**",
                                                "/api/v1/user/oauth2/**"
                                        )
                                        .permitAll()
                                        .requestMatchers(
                                                HttpMethod.GET,
                                                "/api/v1/user/auth"
                                        )
                                        .permitAll()
                                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAutFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
