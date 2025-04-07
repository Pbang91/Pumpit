package com.example.pumpit.global.util;

import com.example.pumpit.global.exception.CustomException;
import com.example.pumpit.global.exception.enums.CustomExceptionData;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 객체 저장 (Value)
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 객체 저장 + TTL 지정
     */
    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * 객체 조회
     */
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) return null;

        try {
            return clazz.cast(value);
        } catch (ClassCastException e) {
            throw new CustomException(
                    CustomExceptionData.INTERVAL_SERVER_ERROR,
                    "Redis value cannot be cast to " + clazz.getSimpleName() + ": " + e.getMessage()
            );
        }
    }

    /**
     * 존재 여부 확인
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 삭제
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
