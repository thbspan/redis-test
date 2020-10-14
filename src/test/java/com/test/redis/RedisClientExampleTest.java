package com.test.redis;

import java.io.IOException;

import org.junit.Test;

public class RedisClientExampleTest {

    @Test
    public void testClient() throws IOException {
        RedisClientExample client = new RedisClientExample("127.0.0.1", 6379);
        client.set("my-client", "my-value");
        System.out.println(client.get("my-client"));

    }
}
