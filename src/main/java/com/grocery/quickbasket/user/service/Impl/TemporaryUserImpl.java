package com.grocery.quickbasket.user.service.Impl;

import com.grocery.quickbasket.user.entity.TemporaryUser;
import com.grocery.quickbasket.user.service.TemporaryUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TemporaryUserImpl implements TemporaryUserService {
    private final RedisTemplate<String, TemporaryUser> redisTemplate;

    public TemporaryUserImpl(RedisTemplate<String, TemporaryUser> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveTemporaryUser(TemporaryUser temporaryUser) {
        redisTemplate.opsForValue().set(temporaryUser.getEmail(), temporaryUser, 1, TimeUnit.MINUTES);
    }

    @Override
    public TemporaryUser getTemporaryUser(String verificationToken) {
        return redisTemplate.opsForValue().get(verificationToken);
    }

    @Override
    public void deleteTemporaryUser(String verificationToken) {
        redisTemplate.delete(verificationToken);
    }
}
