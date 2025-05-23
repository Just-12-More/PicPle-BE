//package com.Just_112_More.PicPle;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//public class RedisTestController {
//
//    private final StringRedisTemplate redisTemplate;
//
//    @GetMapping("/redis/test")
//    public String testRedis() {
//        redisTemplate.opsForValue().set("hello", "world");
//        return redisTemplate.opsForValue().get("hello");  // → "world"
//    }
//}
