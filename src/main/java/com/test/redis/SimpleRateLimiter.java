package com.test.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class SimpleRateLimiter {
    private Jedis jedis;

    public SimpleRateLimiter(Jedis jedis) {
        this.jedis = jedis;
    }

    public boolean isActionAllowed(String userId, String actionKey, int period, int maxCount) {
        String key = String.format("hist:%s:%s", userId, actionKey);
        long now = System.currentTimeMillis();
        try (Pipeline pipeline = jedis.pipelined()) {
            pipeline.multi();
            pipeline.zadd(key, now, Long.toString(now));
            pipeline.zremrangeByScore(key, 0, now - period * 1000);
            Response<Long> count = pipeline.zcard(key);
            pipeline.expire(key, period + 1);
            pipeline.exec();
            return count.get() <= maxCount;
        }
    }
}
