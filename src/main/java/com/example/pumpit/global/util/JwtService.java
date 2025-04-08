package com.example.pumpit.global.util;

import com.example.pumpit.global.exception.CustomException;
import com.example.pumpit.global.exception.enums.CustomExceptionData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.*;

import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
    @Value("${jwt.token.secret}")
    private String SECRET;
    @Value("${jwt.token.access.expiration}")
    private String ACCESS_TOKEN_EXPIRATION;
    @Value("${jwt.token.refresh.expiration}")
    private String REFRESH_TOKEN_EXPIRATION;

    private final UserDetailsService userDetailsService;

    public JwtService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private String createJwt(Map<String, Object> claims, String expirationTime) {
        long expiration;
        long now = System.currentTimeMillis();

        try {
            expiration = Long.parseLong(expirationTime);
        } catch (NumberFormatException e) {
            expiration = 1000L * 60 * 60; // Default to 1 hours if parsing fails
        }

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    private Claims parseJwt(String jwt) {
        return Jwts.parser()
                .setSigningKey(Decoders.BASE64.decode(SECRET))
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    public String generateAccessToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", userId);

        return createJwt(claims, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateAccessToken(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", userId);
        claims.put("role", role);

        return createJwt(claims, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", userId);

        return createJwt(claims, REFRESH_TOKEN_EXPIRATION);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseJwt(token);

        Long userId = claims.get("userId", Long.class);
        String role = claims.get("role", String.class);

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (userId != null) {
            if (role != null) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }

            CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(userId.toString());
            CustomUserDetails customUserDetails = new CustomUserDetails(userDetails.getUser(), authorities);

            return new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);
        }

        throw new CustomException(CustomExceptionData.INTERVAL_SERVER_ERROR, "토큰 정보 오류");
    }
}
