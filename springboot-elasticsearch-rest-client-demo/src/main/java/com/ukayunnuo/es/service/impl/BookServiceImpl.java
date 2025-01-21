package com.ukayunnuo.es.service.impl;

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
