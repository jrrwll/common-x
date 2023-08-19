package org.dreamcat.common.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.dreamcat.common.util.ObjectUtil;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

/**
 * Create by tuke on 2021/3/23
 */
public final class EsRestClientUtil {

    private EsRestClientUtil() {
    }

    // to use the default port, set port = -1
    public static RestClientBuilder restClientBuilder(String host, int port) {
        return restClientBuilder(host, port, null, null);
    }

    public static RestClientBuilder restClientBuilder(String host, int port, String username, String password) {
        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost(host, port));
        configureBasicAuth(restClientBuilder, username, password);
        return restClientBuilder;
    }

    public static RestClientBuilder restClientBuilder(List<String> addresses) {
        return restClientBuilder(addresses, null, null);
    }

    public static RestClientBuilder restClientBuilder(List<String> addresses, String username, String password) {
        HttpHost[] hosts = addresses.stream().map(HttpHost::create).toArray(HttpHost[]::new);
        RestClientBuilder restClientBuilder = RestClient.builder(hosts);
        configureBasicAuth(restClientBuilder, username, password);
        return restClientBuilder;
    }

    private static void configureBasicAuth(RestClientBuilder restClientBuilder, String username, String password) {
        if (ObjectUtil.isNotBlank(username) && ObjectUtil.isNotBlank(password)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            restClientBuilder.setHttpClientConfigCallback(httpClientBuilder ->
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
    }

    // ==== ==== ==== ====    ==== ==== ==== ====    ==== ==== ==== ====

    public static ElasticsearchClient elasticsearchClient(
            String host, int port) {
        return elasticsearchClient(restClientBuilder(host, port));
    }

    public static ElasticsearchClient elasticsearchClient(
            String host, int port, String username, String password) {
        return elasticsearchClient(restClientBuilder(host, port, username, password));
    }

    public static ElasticsearchClient elasticsearchClient(
            List<String> addresses) {
        return elasticsearchClient(restClientBuilder(addresses));
    }

    public static ElasticsearchClient elasticsearchClient(
            List<String> addresses, String username, String password) {
        return elasticsearchClient(restClientBuilder(addresses, username, password));
    }

    public static ElasticsearchClient elasticsearchClient(
            RestClientBuilder restClientBuilder) {
        ElasticsearchTransport transport = new RestClientTransport(
                restClientBuilder.build(), new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
