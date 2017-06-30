package com.gmos.lab.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;

public interface SearchService {


    Page<SearchClimateAnalysis> findByStation(String Station, PageRequest pageRequest);

    List<SearchClimateAnalysis> findByStation(String station);

    List<SearchClimateAnalysis> findByCountry(String title);

    List<SearchClimateAnalysis> findByGEO(String latitude, String longitude);

}
