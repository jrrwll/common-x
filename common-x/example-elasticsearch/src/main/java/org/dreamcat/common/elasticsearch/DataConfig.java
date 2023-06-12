package org.dreamcat.common.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jerry Will
 * @version 2022-04-01
 */
@Configuration
public class DataConfig {

    @Bean
    public ElasticsearchClient elasticsearchClient(
            @Autowired RestClientBuilder restClientBuilder) {
        return EsRestClientUtil.elasticsearchClient(restClientBuilder);
    }
}
