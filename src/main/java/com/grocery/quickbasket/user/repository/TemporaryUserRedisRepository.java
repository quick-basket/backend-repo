package com.grocery.quickbasket.user.repository;

import com.grocery.quickbasket.user.entity.TemporaryUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class TemporaryUserRedisRepository {
    private static final String STRING_KEY_PREFIX = "quickbasket:verifyToken:user:";
    private final ValueOperations<String, String> valueOperations;

    public TemporaryUserRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
    }

    public void saveTemporaryUser(TemporaryUser temporaryUser) {
        valueOperations.set(temporaryUser.getVerificationToken(), String.valueOf(temporaryUser), 1, TimeUnit.MINUTES);
    }

    public String getTemporaryUser(String verificationToken) {
        return valueOperations.get(verificationToken);
    }

}
