package com.example.pumpit.global.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    private final ObjectMapper objectMapper;

    public RedisConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .build();

        GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(2);
        poolConfig.setMaxWait(Duration.ofSeconds(5));

        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration(host, port);

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        ObjectMapper redisMapper = objectMapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
                .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(redisMapper);

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(factory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
