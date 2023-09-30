package com.task.shortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private static final String SHORT_URL_PREFIX = "shortened:";
    private static final String ORIG_URL_PREFIX = "original:";
    private static final long URL_EXPIRE_TIME = 5;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveShortenedKey(String shortenedKey, String originalUrl) {
        redisTemplate.opsForValue().set(SHORT_URL_PREFIX + shortenedKey, originalUrl, URL_EXPIRE_TIME, TimeUnit.HOURS);
    }

    public String getOriginalUrlFromShortened(String shortenedKey) {
        return redisTemplate.opsForValue().get(SHORT_URL_PREFIX + shortenedKey);
    }

    public void saveOriginalUrlMapping(String originalUrl, String shortenedKey) {
        redisTemplate.opsForValue().set(ORIG_URL_PREFIX + originalUrl, shortenedKey, URL_EXPIRE_TIME, TimeUnit.HOURS);
    }

    public String getShortenedKeyFromOriginalUrl(String originalUrl) {
        return redisTemplate.opsForValue().get(ORIG_URL_PREFIX + originalUrl);
    }

}



