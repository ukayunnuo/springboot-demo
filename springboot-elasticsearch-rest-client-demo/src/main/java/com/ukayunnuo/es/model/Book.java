package com.ukayunnuo.es.model;

import lombok.Data;

/**
 * @author yunnuo
 * @since 1.0.0
 */
@Data
public class Book {

    /**
     * 主键
     */
    private Long id;

    /**
     * 书名
     */
    private String name;

    /**
     * 介绍
     */
    private String remark;


}
