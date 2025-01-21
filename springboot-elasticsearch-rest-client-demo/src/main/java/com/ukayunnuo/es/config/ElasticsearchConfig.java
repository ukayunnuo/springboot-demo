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
import org.apache.http.message.BasicHeader;
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
    private ElasticsearchProperties properties;

    private List<HttpHost> getHttpHosts() {
        List<HttpHost> httpHosts = new ArrayList<>();
        List<String> clusterNodes = properties.getClusterNodes();
        clusterNodes.forEach(node -> {
            String[] parts = StringUtils.split(node, ":");
            if (Objects.isNull(parts) || parts.length != 2) {
                throw new IllegalArgumentException("Invalid cluster node: " + node);
            }
            httpHosts.add(new HttpHost(parts[0], Integer.parseInt(parts[1]), properties.getSchema()));
        });
        return httpHosts;
    }

    public RestClient getRestClient() {
        List<HttpHost> httpHosts = getHttpHosts();
        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0]));

        // 请求配置
        builder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                        .setConnectTimeout(properties.getConnectTimeout())
                        .setSocketTimeout(properties.getSocketTimeout())
                        .setConnectionRequestTimeout(properties.getConnectionRequestTimeout())
        );

        // 连接配置
        builder.setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder
                                .setMaxConnTotal(properties.getMaxConnectTotal())
                                .setMaxConnPerRoute(properties.getMaxConnectPerRoute())
//                        .setDefaultHeaders(Arrays.asList(
//                                new BasicHeader("X-Elastic-Product", "Elasticsearch")))
        );

        // 认证授权account
        ElasticsearchProperties.Account account = properties.getAccount();
        if (StringUtils.hasText(account.getUsername()) && StringUtils.hasText(account.getPassword())) {
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(account.getUsername(), account.getPassword()));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        List<BasicHeader> basicHeaders = new ArrayList<>();
        // 是否开启apikey 认证
        if (properties.getAuthEnable()) {
            basicHeaders.add(new BasicHeader("Authorization", "ApiKey " + properties.getApiKey()));
        }
//        basicHeaders.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString()));
//        basicHeaders.add(new BasicHeader("X-Elastic-Product", "Elasticsearch"));

        // 请求头
        builder.setDefaultHeaders(basicHeaders.toArray(new BasicHeader[0]));
        log.info("ElasticsearchClient init! param:{}", JSONObject.toJSONString(properties));

        return builder.build();
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
