package com.task.shortener.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "urls")
public class Url {

    @Id
    private String id;

    private String originalUrl;

    private String shortenedUrl;

    public Url(String originalUrl, String shortenedUrl) {
        this.originalUrl = originalUrl;
        this.shortenedUrl = shortenedUrl;
    }
}
