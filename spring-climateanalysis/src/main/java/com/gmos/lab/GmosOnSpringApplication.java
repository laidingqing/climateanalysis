package com.gmos.lab;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import java.util.Map;

@SpringBootApplication
public class GmosOnSpringApplication implements CommandLineRunner {


	@Autowired
	private ElasticsearchOperations es;

	@Autowired
	private ElasticsearchTemplate esTemplate;

	public static void main(String[] args) {

		SpringApplication.run(GmosOnSpringApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {

		printElasticSearchInfo();

	}

	private void printElasticSearchInfo() {

		System.out.println("--ElasticSearch--");
		Client client = es.getClient();
		Map<String, String> asMap = client.settings().getAsMap();

		asMap.forEach((k, v) -> {
			System.out.println(k + " = " + v);
		});
		System.out.println("--ElasticSearch--");

	}
}
