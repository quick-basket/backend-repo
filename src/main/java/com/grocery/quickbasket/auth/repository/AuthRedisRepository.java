package com.grocery.quickbasket.auth.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class AuthRedisRepository {
    public static final String REGISTRATION_PREFIX = "quickbasket:verification:token:";
    public static final String RESET_PREFIX = "quickbasket:reset:token:";
    private final ValueOperations<String, String> valueOps;

    public AuthRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.valueOps = redisTemplate.opsForValue();
    }

    public void saveVerificationToken(String email, String verificationToken, String prefix) {
        valueOps.set(prefix + verificationToken, email, 1, TimeUnit.HOURS);
    }

    public String getEmail(String verificationToken, String prefix) {
        return valueOps.get(prefix + verificationToken);
    }

    public void deleteVerificationToken(String verificationToken, String prefix) {
        valueOps.getOperations().delete(prefix + verificationToken);
    }
}
