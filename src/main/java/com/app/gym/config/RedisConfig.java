package com.app.gym.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate redisTemplate(@NonNull RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}
