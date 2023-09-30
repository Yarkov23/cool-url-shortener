package com.task.shortener.entity;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@RequiredArgsConstructor
@Document(collection = "urls")
public class Url {

    @Id
    private String id;

    @NonNull
    private String originalUrl;

    @NonNull
    private String shortenedUrl;
}
