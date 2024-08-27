package com.grocery.quickbasket.auth.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class AuthRedisRepository {
    private static final String STRING_KEY_PREFIX = "quickbasket:verification:token:";
    private final ValueOperations<String, String> valueOps;

    public AuthRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.valueOps = redisTemplate.opsForValue();
    }

    public void saveVerificationToken(String email, String verificationToken) {
        valueOps.set(STRING_KEY_PREFIX + verificationToken, email, 1, TimeUnit.HOURS);
    }

    public String getEmail(String verificationToken) {
        return valueOps.get(STRING_KEY_PREFIX + verificationToken);
    }

    public void deleteVerificationToken(String verificationToken) {
        valueOps.getOperations().delete(STRING_KEY_PREFIX + verificationToken);
    }
}
