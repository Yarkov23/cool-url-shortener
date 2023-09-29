package com.task.shortener.repository;

import com.task.shortener.entity.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends MongoRepository<Url, String> {

    Url findByOriginalUrl(String originalUrl);

    Url findByShortenedUrl(String shortenedUrl);

}

