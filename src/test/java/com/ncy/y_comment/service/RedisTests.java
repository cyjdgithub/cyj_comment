package com.ncy.y_comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

@SpringBootTest
public class RedisTests {

    /*@Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String TEST_KEY = "test:key";
    private static final String TEST_VALUE = "Hello, Redis!";

    @BeforeEach
    public void setUp() {
        // 清理 Redis 数据库，确保每次测试都是干净的
        stringRedisTemplate.delete(TEST_KEY);
    }

    @Test
    public void testRedisSetAndGet() {
        // 将数据存储到 Redis
        stringRedisTemplate.opsForValue().set(TEST_KEY, TEST_VALUE);

        // 从 Redis 中获取数据
        String value = stringRedisTemplate.opsForValue().get(TEST_KEY);

        // 断言存储的数据与读取的数据一致
        Assert.isTrue(TEST_VALUE.equals(value), "Redis get returned an incorrect value!");

        System.out.println("Redis存储和获取数据成功：key=" + TEST_KEY + ", value=" + value);
    }

    @Test
    public void testRedisExpire() throws InterruptedException {
        // 存储数据并设置过期时间（例如：2秒）
        stringRedisTemplate.opsForValue().set(TEST_KEY, TEST_VALUE, 2, java.util.concurrent.TimeUnit.SECONDS);

        // 等待超过过期时间
        Thread.sleep(3000);

        // 再次从 Redis 中获取数据
        String value = stringRedisTemplate.opsForValue().get(TEST_KEY);

        // 断言数据已经过期，应该为 null
        Assert.isNull(value, "Redis key should have expired!");

        System.out.println("Redis数据已过期，测试成功！");
    }*/
}
