package com.juyoufuli.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceApplicationTests {

    @Test
    public void contextLoads() {
        RedisTemplate redisTemplate = new RedisTemplate();
        HashOperations hashOperations = redisTemplate.opsForHash();

    }

}
