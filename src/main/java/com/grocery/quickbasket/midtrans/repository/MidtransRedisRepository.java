package com.grocery.quickbasket.midtrans.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.quickbasket.midtrans.MidtransResponse;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;

@Repository
public class MidtransRedisRepository {
    private static final String KEY_PREFIX = "quickbasket:midtrans:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public MidtransRedisRepository(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveMidtransResponse(JSONObject response) {
        String transactionId = response.getString("transaction_id");
        String key = KEY_PREFIX + transactionId;

        // Save to Redis
        redisTemplate.opsForValue().set(key, response.toString());

        // Set expiry
        String expiryTimeStr = response.optString("expiry_time");
        if (!expiryTimeStr.isEmpty()) {
            Instant expiryTime = Instant.parse(expiryTimeStr.replace(" ", "T") + "Z");
            Duration ttl = Duration.between(Instant.now(), expiryTime);
            redisTemplate.expire(key, ttl);
        }
    }

    public JSONObject getMidtransResponse(String transactionId) {
        String key = KEY_PREFIX + transactionId;
        String jsonResponse = redisTemplate.opsForValue().get(key);

        if (jsonResponse != null) {
            return new JSONObject(jsonResponse);
        }

        return null;
    }

    public void deleteMidtransResponse(String transactionId) {
        String key = KEY_PREFIX + transactionId;
        redisTemplate.delete(key);
    }
}