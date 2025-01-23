package com.ukayunnuo.es.controller;

import com.alibaba.fastjson2.JSONObject;
import com.ukayunnuo.es.constants.EsConstant;
import com.ukayunnuo.es.core.Result;
import com.ukayunnuo.es.model.Book;
import com.ukayunnuo.es.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.ukayunnuo.es.utils.EsIndexUtil;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/demo/es/boot")
public class EsBootTestController {

    @Resource
    private EsIndexUtil esUtil;

    @Resource
    private BookService bookService;

    /**
     * 创建索引index
     *
     * @return 创建结果
     */
    @GetMapping("/createIndex")
    public Result<Boolean> createIndex() {
        log.info("创建索引 index book");
        return Result.success(esUtil.createIndex(EsConstant.INDEX_NAME_BOOK));
    }

    /**
     * 删除索引
     *
     * @return 删除结果
     */
    @GetMapping("/delIndex")
    public Result<Boolean> delIndex() {
        log.info("删除索引 index book");
        return Result.success(esUtil.delIndex(EsConstant.INDEX_NAME_BOOK));
    }


    /**
     * 插入数据
     *
     * @param book 数据
     * @return 结果
     */
    @PostMapping("/insert")
    public Result<Boolean> insertBook(@RequestBody Book book) {
        log.info("创建es数据 document book:{}", JSONObject.toJSONString(book));
        return Result.success(bookService.insert(book));
    }

    /**
     * 更新数据
     *
     * @param book 数据
     * @return 结果
     */
    @PostMapping("/update")
    public Result<Boolean> updateBookById(@RequestBody Book book) {
        log.info("更新es数据 document book:{}", JSONObject.toJSONString(book));
        return Result.success(bookService.update(book));
    }

    /**
     * 通过id查询数据
     *
     * @param bootId id
     * @return 结果
     */
    @GetMapping("/findById/{bootId}")
    public Result<List<Book>> findBookById(@PathVariable Long bootId) {
        log.info("查询es数据 bootId:{}", bootId);
        return Result.success(Collections.singletonList(bookService.findById(bootId)));
    }

    /**
     * 通过字段-名称查询数据
     *
     * @param bootName 字段-名称
     * @return 结果
     */
    @GetMapping("/findByBookName/{bootName}")
    public Result<List<Book>> findByBookName(@PathVariable String bootName) {
        log.info("查询es数据 bootName:{}", bootName);
        return Result.success(bookService.findByBookName(bootName));
    }

    /**
     * 通过id删除数据
     *
     * @param bootId id
     * @return 结果
     */
    @GetMapping("/delete/{bootId}")
    public Result<Boolean> deleteBookById(@PathVariable Long bootId) {
        log.info("删除es数据 bootId:{}", bootId);
        return Result.success(bookService.delete(bootId));
    }

}