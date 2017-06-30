package com.gmos.lab.search;

import com.gmos.lab.GmosOnSpringApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GmosOnSpringApplication.class)
public class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Before
    public void before() {
        esTemplate.putMapping(SearchClimateAnalysis.class);
        esTemplate.refresh(SearchClimateAnalysis.class);
    }

    @Test
    public void testFindByStation() {
        List<SearchClimateAnalysis> byStation = searchService.findByStation("CA007056202");
        assertThat(byStation.size(), is(236));
    }

    @Test
    public void testFindByStationPage() {

        Page<SearchClimateAnalysis> byAuthor = searchService.findByStation("CA007056202", new PageRequest(0, 10));
        assertThat(byAuthor.getTotalElements(), is(236L));

    }

    @Test
    public void testFindByGEO() {
        List<SearchClimateAnalysis> byGEO = searchService.findByGEO("49.8333","-64.3");
        assertThat(byGEO.size(), is(10));
    }

}
