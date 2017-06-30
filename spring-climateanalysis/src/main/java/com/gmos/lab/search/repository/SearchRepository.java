package com.gmos.lab.search.repository;

import com.gmos.lab.search.SearchClimateAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface SearchRepository extends ElasticsearchRepository<SearchClimateAnalysis, String> {

    Page<SearchClimateAnalysis> findByStation(String station, Pageable pageable);

    List<SearchClimateAnalysis> findByStation(String station);

    List<SearchClimateAnalysis> findByCountry(String country);

}