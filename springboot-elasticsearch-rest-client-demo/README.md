# Springboot集成Elasticsearch8.0（ES）版本，采用JAVA Client方式进行连接和实现CRUD操作

> 在ES7.15版本之后，ES官方将高级客户端 `RestHighLevelClient`标记为弃用状态。同时推出了全新的 Java API客户端 Elasticsearch Java API Client，该客户端也将在 Elasticsearch8.0及以后版本中成为官方推荐使用的客户端。

## ES官方文档：
- Elasticsearch Clients文档：[https://www.elastic.co/guide/en/elasticsearch/client/index.html](https://www.elastic.co/guide/en/elasticsearch/client/index.html)
- JAVA Client 8.0文档: [https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/getting-started-java.html](https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/getting-started-java.html)

## Pom依赖

```xml

<properties>
    <java.version>1.8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <lombok.version>1.18.28</lombok.version>
    <fastjson2.version>2.0.34</fastjson2.version>
    <hutool.version>5.8.21</hutool.version>
    <es.version>8.17.0</es.version>
    <jakarta.version>2.1.3</jakarta.version>
    <jackson.version>2.17.0</jackson.version>
</properties>

<dependencies>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>${lombok.version}</version>
</dependency>

<!-- fastjson2 -->
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>${fastjson2.version}</version>
</dependency>

<!--    hutool工具类    -->
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>${hutool.version}</version>
</dependency>

<!-- elasticsearch -->
<dependency>
    <groupId>co.elastic.clients</groupId>
    <artifactId>elasticsearch-java</artifactId>
    <version>${es.version}</version>
    <exclusions>
        <exclusion>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-client</artifactId>
        </exclusion>
        <exclusion>
            <groupId>jakarta.json</groupId>
            <artifactId>jakarta.json-api</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.elasticsearch.client</groupId>
    <artifactId>elasticsearch-rest-client</artifactId>
    <version>${es.version}</version>
</dependency>

<!-- jakarta -->
<dependency>
    <groupId>jakarta.json</groupId>
    <artifactId>jakarta.json-api</artifactId>
    <version>${jakarta.version}</version>
</dependency>
<!-- jackson-databind -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>${jackson.version}</version>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>${jackson.version}</version>
</dependency>

</dependencies>

```

## yaml配置

```yaml

spring:
  data:
    es:
      cluster-name: es-docker-cluster
      cluster-nodes:
        - 127.0.0.1:9200
      # 认证账户
      account:
        password:
        username:
      index:
        # 分片数量
        number-of-replicas: 0
        # 副本数量
        number-of-shards: 3
      # 连接超时时间(毫秒)
      connect-timeout: 1000
      # socket 超时时间(毫秒)
      socket-timeout: 3000
      # 连接请求超时时间(毫秒)
      connection-request-timeout: 500
      # 每个路由的最大连接数量
      max-connect-per-route: 10
      # 最大连接总数量
      max-connect-total: 30
      # 是否开启apikey 认证
      auth-enable: false
      api-key: VnVhQ2ZHY0JDZGJrUUSOWN2

```

## 配置类

### ES 配置信息类

```java
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ES 配置信息
 *
 * @author yunnuo
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.data.es")
public class ElasticsearchProperties {

    /**
     * 请求协议
     */
    private String schema = "http";

    /**
     * 集群名称
     */
    private String clusterName = "elasticsearch";

    /**
     * 集群节点
     */
    private List<String> clusterNodes = new ArrayList<>();

    /**
     * 连接超时时间(毫秒)
     */
    private Integer connectTimeout = 1000;

    /**
     * socket 超时时间(毫秒)
     */
    private Integer socketTimeout = 30000;

    /**
     * 连接请求超时时间(毫秒)
     */
    private Integer connectionRequestTimeout = 500;

    /**
     * 每个路由的最大连接数量
     */
    private Integer maxConnectPerRoute = 10;

    /**
     * 最大连接总数量
     */
    private Integer maxConnectTotal = 30;

    /**
     * 索引配置信息
     */
    private Index index = new Index();

    /**
     * 认证账户
     */
    private Account account = new Account();

    /**
     * api key 认证
     */
    private Boolean authEnable = false;


    private String apiKey;

    /**
     * 索引配置信息
     */
    @Data
    public static class Index {

        /**
         * 分片数量
         */
        private Integer numberOfShards = 3;

        /**
         * 副本数量
         */
        private Integer numberOfReplicas = 2;

    }

    /**
     * 认证账户
     */
    @Data
    public static class Account {

        /**
         * 认证用户
         */
        private String username;

        /**
         * 认证密码
         */
        private String password;

    }

}

```

### 核心配置类

```java
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

```

## 具体实现类

### 文档document操作类

```java
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.es.constants.EsConstant;
import com.ukayunnuo.es.core.ErrorCode;
import com.ukayunnuo.es.core.exception.ServiceException;
import com.ukayunnuo.es.model.Book;
import com.ukayunnuo.es.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ES boot index 服务实现类
 *
 * @author hxt <a href="xthe3257@cggc.cn">Email: xthe3257@cggc.cn </a>
 * @since 1.0.0
 */
@Slf4j
@Service
public class BookServiceImpl implements BookService {

    @Resource
    private ElasticsearchClient esClient;


    /**
     * 通过索引id查询
     *
     * @param id id
     * @return book
     */
    @Override
    public Book findById(Long id) {
        try {
            GetResponse<Book> response = esClient.get(g ->
                    g.index(EsConstant.INDEX_NAME_BOOK).id(String.valueOf(id)), Book.class);
            if (!response.found()) {
                log.warn("未查询到数据 id:{}", id);
            }
            return response.source();
        } catch (Exception e) {
            log.error("查询数据异常 id:{}, e:{}", id, e.getMessage(), e);
            throw new ServiceException(ErrorCode.ES_ERROR);
        }
    }

    /**
     * 通过书名查询
     *
     * @param bookName 书名
     * @return book 列表
     */
    @Override
    public List<Book> findByBookName(String bookName) {
        try {
            SearchResponse<Book> response = esClient.search(s ->
                    s.index(EsConstant.INDEX_NAME_BOOK)
                            .query(q -> q.match(m -> m.field("name")
                                    .query(bookName))), Book.class);
            log.info("查询数据 response:{}", response.toString());
            HitsMetadata<Book> hits = response.hits();
            if (hits == null || hits.hits().isEmpty()) {
                return new ArrayList<>();
            }
            return hits.hits().stream().map(Hit::source).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询数据异常 bookName:{}, e:{}", bookName, e.getMessage(), e);
            throw new ServiceException(ErrorCode.ES_ERROR);
        }
    }


    /**
     * 插入书籍数据
     *
     * @param book 书籍数据
     * @return 插入结果
     */
    @Override
    public Boolean insert(Book book) {
        try {
            IndexResponse response = esClient.index(i -> i.index(EsConstant.INDEX_NAME_BOOK).id(String.valueOf(book.getId())).document(book));
            log.info("新增数据 response:{}", response.result().jsonValue());
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("新增数据异常 book:{}, e:{}", JSONObject.toJSONString(book), e.getMessage(), e);
            throw new ServiceException(ErrorCode.ES_ERROR);
        }
    }

    /**
     * 更新数据
     *
     * @param book book
     * @return 更新结果
     */
    @Override
    public Boolean update(Book book) {
        try {
            UpdateResponse<Book> response = esClient.update(u -> u.index(EsConstant.INDEX_NAME_BOOK).id(String.valueOf(book.getId())).upsert(book), Book.class);
            log.info("更新数据 response:{}", response.result().jsonValue());
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("更新数据异常 book:{}, e:{}", JSONObject.toJSONString(book), e.getMessage(), e);
            throw new ServiceException(ErrorCode.ES_ERROR);
        }
    }

    /**
     * 删除数据
     *
     * @param id 索引id
     * @return 删除结果
     */
    @Override
    public Boolean delete(Long id) {
        try {
            DeleteResponse response = esClient.delete(d -> d.index(EsConstant.INDEX_NAME_BOOK).id(String.valueOf(id)));
            log.info("删除数据 response:{}", response.result().jsonValue());
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("删除数据异常 id:{}, e:{}", id, e.getMessage(), e);
            throw new ServiceException(ErrorCode.ES_ERROR);
        }
    }

}
```

### 索引index操作类

```java
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.ukayunnuo.es.core.ErrorCode;
import com.ukayunnuo.es.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * ES 索引 index 相关工具
 *
 * @author hxt <a href="xthe3257@cggc.cn">Email: xthe3257@cggc.cn </a>
 * @since 1.0.0
 */
@Slf4j
@Component
public class EsIndexUtil {

    @Resource
    private ElasticsearchClient esClient;

    /**
     * 创建索引 index
     *
     * @param indexName 索引名称
     * @return 创建结果
     */
    public Boolean createIndex(String indexName) {
        try {
            esClient.indices().create(c -> c.index(indexName));
            return true;
        } catch (Exception e) {
            log.error("Es创建索引失败 indexName:{}, e:{}", indexName, e.getMessage(), e);
            throw new ServiceException(ErrorCode.ES_ERROR);
        }
    }

    /**
     * 删除索引 index
     *
     * @param indexName 索引名称
     * @return 删除结果
     */
    public Boolean delIndex(String indexName) {
        try {
            esClient.indices().delete(d -> d.index(indexName));
            return true;
        } catch (Exception e) {
            log.error("Es删除索引失败 indexName:{}, e:{}", indexName, e.getMessage(), e);
            throw new ServiceException(ErrorCode.ES_ERROR);
        }
    }
}
```


