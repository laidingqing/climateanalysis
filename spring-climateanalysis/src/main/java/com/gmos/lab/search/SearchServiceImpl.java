package com.gmos.lab.search;

import com.gmos.lab.search.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import java.util.List;
import static org.elasticsearch.index.query.QueryBuilders.*;


@Service
public class SearchServiceImpl implements SearchService {

    private SearchRepository repository;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    public void setSearchRepository(SearchRepository repository) {
        this.repository = repository;
    }

    public Page<SearchClimateAnalysis> findByStation(String station, PageRequest pageRequest) {
        return repository.findByStation(station, pageRequest);
    }

    public List<SearchClimateAnalysis> findByStation(String station) {
        return repository.findByStation(station);
    }

    public List<SearchClimateAnalysis> findByCountry(String country){
        return repository.findByCountry(country);
    }

    public List<SearchClimateAnalysis> findByGEO(String latitude, String longitude){
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("latitude", latitude))
                .withQuery(matchQuery("longitude", longitude))
                .build();
        return esTemplate.queryForList(searchQuery, SearchClimateAnalysis.class);
    }
}