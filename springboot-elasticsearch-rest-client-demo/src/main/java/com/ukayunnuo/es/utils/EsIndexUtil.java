package com.ukayunnuo.es.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.ukayunnuo.es.core.ErrorCode;
import com.ukayunnuo.es.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * ES 索引 index 相关工具
 *
 * @author yunnuo
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
