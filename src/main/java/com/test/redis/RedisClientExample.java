package com.test.redis;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RedisClientExample {
    private Socket socket;
    private OutputStream write;
    private InputStream read;

    public RedisClientExample(String host, int port) throws IOException {
        socket = new Socket(host, port);
        write = socket.getOutputStream();
        read = socket.getInputStream();
    }

    public void set(String key, String val) throws IOException {
        StringBuilder builder = new StringBuilder();
        // 代表3个参数
        builder.append("*3").append("\r\n");
        // 第一个参数(set)的长度
        builder.append("$3").append("\r\n");
        // 第一个参数的内容
        builder.append("SET").append("\r\n");
        // 第二个参数(key)的长度
        builder.append("$").append(key.getBytes(StandardCharsets.UTF_8).length).append("\r\n");
        // 第二个参数内容
        builder.append(key).append("\r\n");
        // 第三个参数value的长度
        builder.append("$").append(val.getBytes(StandardCharsets.UTF_8).length).append("\r\n");
        // 第三个参数内容
        builder.append(val).append("\r\n");
        write.write(builder.toString().getBytes(StandardCharsets.UTF_8));
        byte[] bytes = new byte[1024];
        read.read(bytes);
        System.out.println(new String(bytes, StandardCharsets.UTF_8));
    }

    public String get(String key) throws IOException {
        StringBuilder builder = new StringBuilder();
        // 代表2个参数
        builder.append("*2").append("\r\n");
        // 第一个参数(get)的长度
        builder.append("$3").append("\r\n");
        // 第一个参数的内容
        builder.append("GET").append("\r\n");
        // 第二个参数(key)的长度
        builder.append("$").append(key.getBytes(StandardCharsets.UTF_8).length).append("\r\n");
        // 第二个参数内容
        builder.append(key).append("\r\n");
        write.write(builder.toString().getBytes(StandardCharsets.UTF_8));
        byte[] bytes = new byte[1024];
        read.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
