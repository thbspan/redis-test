package com.test.redis;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisLuaTest {
    private static Jedis jedis;

    @BeforeClass
    public static void beforeClass() {
        jedis = new Jedis();
    }

    @Test
    public void testSimple() {
        System.out.println(jedis.eval("return 'Hello Lua'"));
    }

    @Test
    public void testCall() {
        jedis.eval("redis.call('set', KEYS[1], ARGV[1])", 1, "test-key", "test-value");
        System.out.println(jedis.get("test-key"));
    }

    @Test
    public void testScriptSha1() {
        String sha1 = jedis.scriptLoad("redis.call('set', KEYS[1], ARGV[1])");
        jedis.evalsha(sha1, 1, "sha", "value1");
        System.out.println(jedis.get("sha"));
    }

    @Test
    public void testCall2() {
        System.out.println(jedis.eval("return redis.call('ZRANGEBYSCORE', KEYS[1], 0, ARGV[1], 'limit', 0, 1)", 1, "q-demo", Long.toString(1569569870108L)));
    }

    @AfterClass
    public static void afterClass() {
        jedis.close();
    }
}
