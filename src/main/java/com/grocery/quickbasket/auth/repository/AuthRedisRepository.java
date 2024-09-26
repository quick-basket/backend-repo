package com.grocery.quickbasket.auth.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class AuthRedisRepository {
    public static final String REGISTRATION_PREFIX = "quickbasket:verification:token:";
    public static final String RESET_PREFIX = "quickbasket:reset:token:";
    public static final String BLACKLIST_PREFIX = "quickbasket:blacklist:token:";

    private final ValueOperations<String, String> valueOps;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthRedisRepository(RedisTemplate<String, String> redisTemplate, RedisTemplate<String, String> redisTemplate1) {
        this.valueOps = redisTemplate.opsForValue();
        this.redisTemplate = redisTemplate1;
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

    public void blacklistToken(String token){
        valueOps.set(BLACKLIST_PREFIX + token, "blacklisted", 12, TimeUnit.HOURS);
    }

    public boolean isTokenBlacklisted(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}

