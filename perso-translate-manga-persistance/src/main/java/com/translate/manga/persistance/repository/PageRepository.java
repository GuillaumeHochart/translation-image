package com.translate.manga.persistance.repository;

import com.translate.manga.persistance.PageJson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends MongoRepository<PageJson,Long> {

    public List<PageJson> getByFilename(String filename);

    public PageJson getByKey(String key);
}
