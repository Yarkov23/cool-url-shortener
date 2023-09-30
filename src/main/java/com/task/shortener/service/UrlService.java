package com.task.shortener.service;


import com.task.shortener.entity.Url;
import com.task.shortener.repository.UrlRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
    private final UrlRepository urlRepository;
    private final RedisService redisService;
    private final ShorteningService shorteningService;

    @Autowired
    public UrlService(UrlRepository urlRepository, RedisService redisService, ShorteningService shorteningService) {
        this.urlRepository = urlRepository;
        this.redisService = redisService;
        this.shorteningService = shorteningService;
    }

    public String getShortenedUrl(String originalUrl) {
        logger.info("Attempting to fetch or create shortened URL for: {}", originalUrl);

        if (StringUtils.isEmpty(originalUrl)) {
            throw new IllegalArgumentException("Original URL must not be null or empty");
        }

        String shortenedKey = redisService.getShortenedKeyFromOriginalUrl(originalUrl);

        if (shortenedKey != null) {
            logger.debug("Found shortened URL in cache for: {}", originalUrl);
            return shortenedKey;
        }

        Url url = urlRepository.findByOriginalUrl(originalUrl);
        if (url == null) {
            url = saveUrl(originalUrl);
        }

        cacheUrlMappings(url.getOriginalUrl(), url.getShortenedUrl());
        logger.info("Successfully fetched or created shortened URL for: {}", originalUrl);
        return url.getShortenedUrl();
    }

    public String getOriginalUrl(String shortenedUrl) {
        logger.info("Attempting to resolve original URL for: {}", shortenedUrl);
        if (StringUtils.isEmpty(shortenedUrl)) {
            throw new IllegalArgumentException("Shortened URL must not be null or empty");
        }

        String originalUrl = redisService.getOriginalUrlFromShortened(shortenedUrl);
        if (originalUrl != null) {
            logger.debug("Found original URL in cache for: {}", shortenedUrl);
            return originalUrl;
        }

        Url url = urlRepository.findByShortenedUrl(shortenedUrl);

        if (url != null) {
            cacheUrlMappings(url.getOriginalUrl(), shortenedUrl);
        }

        logger.info("Resolved original URL for: {}", shortenedUrl);

        return url != null ? url.getOriginalUrl() : "";
    }

    private Url saveUrl(String originalUrl) {
        logger.info("Saving original URL to database: {}", originalUrl);

        String shortenedUrl = shorteningService.generateShortenedUrl();
        Url url = new Url(originalUrl, shortenedUrl);
        urlRepository.save(url);
        cacheUrlMappings(originalUrl, shortenedUrl);

        logger.info("Successfully saved URL: {}", originalUrl);
        return url;
    }

    private void cacheUrlMappings(String original, String shortened) {
        redisService.saveShortenedKey(shortened, original);
        redisService.saveOriginalUrlMapping(original, shortened);
    }
}

