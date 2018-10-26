package com.translate.manga.persistance.repository;

import com.translate.manga.persistance.PageJson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PageRepository extends MongoRepository<PageJson,Long> {

    public PageJson getByFilename(String filename);
}
