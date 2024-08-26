package com.grocery.quickbasket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.quickbasket.user.entity.TemporaryUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, TemporaryUser> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, TemporaryUser> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        Jackson2JsonRedisSerializer<TemporaryUser> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, TemporaryUser.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }
}