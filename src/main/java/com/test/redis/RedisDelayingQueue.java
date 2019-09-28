package com.test.redis;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import redis.clients.jedis.Jedis;

public class RedisDelayingQueue<T> {
    static class TaskItem<T> {
        public String id;
        public T msg;
    }

    private final Type TYPE_TASK = new TypeReference<TaskItem<T>>() {}.getType();

    private Jedis jedis;
    private String queueKey;

    public RedisDelayingQueue(Jedis jedis, String queueKey) {
        this.jedis = jedis;
        this.queueKey = queueKey;
    }

    public void delay(T msg) {
        TaskItem<T> task = new TaskItem<>();
        task.id = UUID.randomUUID().toString();
        task.msg = msg;
        jedis.zadd(queueKey, System.currentTimeMillis() + 5000, JSON.toJSONString(task));
    }

    public void loop() {
        while (Thread.interrupted()) {
            Set<String> values = jedis.zrangeByScore(queueKey, 0, System.currentTimeMillis(), 0, 1);
            if (values.isEmpty()) {
                try {
                    Thread.sleep(500); // 歇会继续
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            String s = values.iterator().next();
            if (jedis.zrem(queueKey, s) > 0) { // 抢到了
                TaskItem<T> task = JSON.parseObject(s, TYPE_TASK); // fastjson 反序列化
                this.handleMsg(task.msg);
            }
        }
    }

    public void loopByLua() {
        while (!Thread.interrupted()) {
            String s = (String) jedis.eval("local values = redis.call('ZRANGEBYSCORE', KEYS[1], 0, ARGV[1], 'limit', 0, 1)\n" +
                    "if values == nil then\n" +
                    "    return nil\n" +
                    "end\n" +
                    "redis.call('ZREM', KEYS[1], values[1])\n" +
                    "return values[1]", 1, queueKey, Long.toString(System.currentTimeMillis()));
            if (s == null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }
            // fastjson 反序列化
            TaskItem<T> task = JSON.parseObject(s, TYPE_TASK);
            this.handleMsg(task.msg);
        }
    }

    public void handleMsg(T msg) {
        System.out.println(msg);
    }

}
