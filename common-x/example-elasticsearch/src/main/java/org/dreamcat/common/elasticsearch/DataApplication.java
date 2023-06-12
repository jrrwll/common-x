package org.dreamcat.common.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import javax.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Create by tuke on 2021/1/15
 * -Dspring.elasticsearch.rest.uris=http://192.168.1.255:9200
 */
@SpringBootApplication
public class DataApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataApplication.class, args);
    }

    @Resource
    private ElasticsearchClient elasticsearchClient;

    @Bean
    public EsIndexClient esIndexClient() {
        return new EsIndexClient(elasticsearchClient);
    }

    @Bean
    public EsDocClient esDocClient() {
        return new EsDocClient(elasticsearchClient);
    }

    @Bean
    public EsQueryClient esQueryClient() {
        return new EsQueryClient(elasticsearchClient);
    }
}
