package com.task.shortener.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShorteningService {

    private static final String BASE_URL = "http://str/";

    public String generateShortenedUrl() {
        String randomPart = UUID.randomUUID().toString().substring(0, 6);
        return BASE_URL + randomPart;
    }

}

