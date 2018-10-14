package com.ote.test;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricElasticsearchRespository extends ElasticsearchCrudRepository<MetricDocument, Integer> {
}
