package com.house.houseviewing.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTestService {

    private final StringRedisTemplate stringRedisTemplate;

    public void saveTestValue(){
        stringRedisTemplate.opsForValue().set("test-key", "hello-redis", Duration.ofMinutes(5));
    }

    public String getTestValue(){
        return stringRedisTemplate.opsForValue().get("test-key");
    }
}
