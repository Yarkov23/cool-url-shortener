package com.task.shortener.controller;

import com.task.shortener.entity.Url;
import com.task.shortener.service.UrlService;
import com.task.shortener.util.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<String> getShortenedUrl(@RequestBody Url urlRequest) {
        if (!UrlValidator.isValid(urlRequest.getOriginalUrl())) {
            return new ResponseEntity<>("Invalid original URL provided", HttpStatus.BAD_REQUEST);
        }

        Url url = urlService.getOrCreateShortenedUrl(urlRequest.getOriginalUrl());
        return new ResponseEntity<>(url.getShortenedUrl(), HttpStatus.OK);
    }

    @GetMapping("/resolve")
    public ResponseEntity<String> getOriginalUrl(@RequestParam String shortenedUrl) {
        if (shortenedUrl == null || shortenedUrl.isEmpty()) {
            return new ResponseEntity<>("Invalid shortened URL provided", HttpStatus.BAD_REQUEST);
        }

        Url url = urlService.getOriginalUrl(shortenedUrl);

        if (url == null) {
            return new ResponseEntity<>("Shortened URL not found or URL is invalid", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(url.getOriginalUrl(), HttpStatus.OK);
    }

}
