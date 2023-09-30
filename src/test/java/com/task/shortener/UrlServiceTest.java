package com.task.shortener;

import com.task.shortener.entity.Url;
import com.task.shortener.repository.UrlRepository;
import com.task.shortener.service.RedisService;
import com.task.shortener.service.ShorteningService;
import com.task.shortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private RedisService redisService;

    @Mock
    private ShorteningService shorteningService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetShortenedUrl_foundInCache() {
        String originalUrl = "http://example.com";
        String shortenedKey = "abc123";

        when(redisService.getShortenedKeyFromOriginalUrl(originalUrl)).thenReturn(shortenedKey);

        String result = urlService.getShortenedUrl(originalUrl);

        assertEquals(shortenedKey, result);
        verify(redisService).getShortenedKeyFromOriginalUrl(originalUrl);
        verify(urlRepository, never()).findByOriginalUrl(anyString());
    }

    @Test
    void testGetShortenedUrl_foundInDatabase() {
        String originalUrl = "http://example.com";
        String shortenedUrl = "abc123";

        when(redisService.getShortenedKeyFromOriginalUrl(originalUrl)).thenReturn(null);
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(new Url(originalUrl, shortenedUrl));

        String result = urlService.getShortenedUrl(originalUrl);

        assertEquals(shortenedUrl, result);
        verify(redisService).getShortenedKeyFromOriginalUrl(originalUrl);
        verify(urlRepository).findByOriginalUrl(originalUrl);
    }

    @Test
    void testGetShortenedUrl_needsSaving() {
        String originalUrl = "http://example.com";
        String generatedShortenedUrl = "xyz789";

        when(redisService.getShortenedKeyFromOriginalUrl(originalUrl)).thenReturn(null);
        when(urlRepository.findByOriginalUrl(originalUrl)).thenReturn(null);
        when(shorteningService.generateShortenedUrl()).thenReturn(generatedShortenedUrl);

        String result = urlService.getShortenedUrl(originalUrl);

        assertEquals(generatedShortenedUrl, result);
        verify(redisService).getShortenedKeyFromOriginalUrl(originalUrl);
        verify(urlRepository).findByOriginalUrl(originalUrl);
        verify(shorteningService).generateShortenedUrl();
    }

    @Test
    void testGetOriginalUrl_foundInCache() {
        String shortenedUrl = "abc123";
        String originalUrl = "http://example.com";

        when(redisService.getOriginalUrlFromShortened(shortenedUrl)).thenReturn(originalUrl);

        String result = urlService.getOriginalUrl(shortenedUrl);

        assertEquals(originalUrl, result);
        verify(redisService).getOriginalUrlFromShortened(shortenedUrl);
        verify(urlRepository, never()).findByShortenedUrl(anyString());
    }

    @Test
    void testGetOriginalUrl_foundInDatabase() {
        String shortenedUrl = "abc123";
        String originalUrl = "http://example.com";

        when(redisService.getOriginalUrlFromShortened(shortenedUrl)).thenReturn(null);
        when(urlRepository.findByShortenedUrl(shortenedUrl)).thenReturn(new Url(originalUrl, shortenedUrl));

        String result = urlService.getOriginalUrl(shortenedUrl);

        assertEquals(originalUrl, result);
        verify(redisService).getOriginalUrlFromShortened(shortenedUrl);
        verify(urlRepository).findByShortenedUrl(shortenedUrl);
    }

    @Test
    void testGetOriginalUrl_notFoundAnywhere() {
        String shortenedUrl = "abc123";

        when(redisService.getOriginalUrlFromShortened(shortenedUrl)).thenReturn(null);
        when(urlRepository.findByShortenedUrl(shortenedUrl)).thenReturn(null);

        String result = urlService.getOriginalUrl(shortenedUrl);

        assertEquals(result, "");
        verify(redisService).getOriginalUrlFromShortened(shortenedUrl);
        verify(urlRepository).findByShortenedUrl(shortenedUrl);
    }
}

