package com.test.redis;

import org.junit.BeforeClass;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class RedisDelayingQueueTest {

    private static Jedis jedis;

    @BeforeClass
    public static void beforeClass() {
        jedis = new Jedis();
    }

    @Test
    public void testQueue() {
        RedisDelayingQueue<String> queue = new RedisDelayingQueue<>(jedis, "q-demo");

        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                queue.delay("codehole" + i);
            }
        }, "producer");

        Thread consumer = new Thread(queue::loopByLua, "consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            Pipeline pipeline = jedis.pipelined();
            pipeline.sync();
            Thread.sleep(6000);
            consumer.interrupt();
            consumer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
