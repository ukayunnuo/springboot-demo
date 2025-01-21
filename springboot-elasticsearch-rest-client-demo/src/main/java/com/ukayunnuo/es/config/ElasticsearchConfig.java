package com.ukayunnuo.es.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * ES 配置类
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchConfig {

    @Resource
    private ElasticsearchProperties elasticsearchProperties;


    public RestClient getRestClient() {
        List<HttpHost> httpHosts = getHttpHosts();
        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0]));

        // 请求配置
        builder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                        .setConnectTimeout(elasticsearchProperties.getConnectTimeout())
                        .setSocketTimeout(elasticsearchProperties.getSocketTimeout())
                        .setConnectionRequestTimeout(elasticsearchProperties.getConnectionRequestTimeout())
        );

        // 连接配置
        builder.setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder
                                .setMaxConnTotal(elasticsearchProperties.getMaxConnectTotal())
                                .setMaxConnPerRoute(elasticsearchProperties.getMaxConnectPerRoute())
//                        .setDefaultHeaders(Arrays.asList(
//                                new BasicHeader("X-Elastic-Product", "Elasticsearch")))
        );

        // 认证授权
        ElasticsearchProperties.Account account = elasticsearchProperties.getAccount();
        if (StringUtils.hasText(account.getUsername()) && StringUtils.hasText(account.getPassword())) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(account.getUsername(), account.getPassword()));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }

        // 请求头
       /* builder.setDefaultHeaders(
                new BasicHeader[]{
                        new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString()),
                        new BasicHeader("X-Elastic-Product", "Elasticsearch"),
                });
*/
        log.info("ElasticsearchClient init! param:{}", JSONObject.toJSONString(elasticsearchProperties));

        return builder.build();
    }

    private List<HttpHost> getHttpHosts() {
        List<HttpHost> httpHosts = new ArrayList<>();
        List<String> clusterNodes = elasticsearchProperties.getClusterNodes();
        clusterNodes.forEach(node -> {
            String[] parts = StringUtils.split(node, ":");
            if (Objects.isNull(parts) || parts.length != 2) {
                throw new IllegalArgumentException("Invalid cluster node: " + node);
            }
            httpHosts.add(new HttpHost(parts[0], Integer.parseInt(parts[1]), elasticsearchProperties.getSchema()));
        });
        return httpHosts;
    }

    @Bean
    @ConditionalOnMissingBean
    public ElasticsearchTransport getElasticsearchTransport() {
        return new RestClientTransport(getRestClient(), new JacksonJsonpMapper());
    }


    @Bean
    @ConditionalOnMissingBean
    public ElasticsearchClient getElasticsearchClient() {
        return new ElasticsearchClient(getElasticsearchTransport());
    }

}
