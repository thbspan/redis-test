package com.test.redis;

import java.util.List;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class RedisScanTest {

    @Test
    public void init() {
        Jedis jedis = new Jedis();
        for (int i = 0; i < 10000; i++) {
            jedis.set("test" + i, Integer.toString(i));
        }
        jedis.close();
    }

    @Test
    public void testScan() {
        try (Jedis jedis = new Jedis()) {
            ScanParams scanParams = new ScanParams();
            scanParams.match("test99*");
            scanParams.count(1000);
            String cursor = "0";
            int count = 0;
            do {
                ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                cursor = scanResult.getCursor();
                List<String> result = scanResult.getResult();
                System.out.println(result);
                count += result.size();
            } while (!"0".equals(cursor));
            System.out.println("count: " + count);
        }
    }
}
