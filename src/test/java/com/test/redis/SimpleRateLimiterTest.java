package com.test.redis;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class SimpleRateLimiterTest {
    private static Jedis jedis;

    @BeforeClass
    public static void beforeClass() {
        jedis = new Jedis();
    }

    @Test
    public void testIsActionAllowed() {
        SimpleRateLimiter limiter = new SimpleRateLimiter(jedis);
        for (int i = 0; i < 7; i++) {
            System.out.println(limiter.isActionAllowed("jack", "publish", 60, 5));
        }
    }

    @AfterClass
    public static void afterClass() {
        jedis.close();
    }
}
