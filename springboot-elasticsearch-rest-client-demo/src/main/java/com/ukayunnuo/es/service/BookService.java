package com.ukayunnuo.es.service;

import com.ukayunnuo.es.model.Book;

import java.util.List;

/**
 * @author hxt <a href="xthe3257@cggc.cn">Email: xthe3257@cggc.cn </a>
 * @since 1.0.0
 */
public interface BookService {

    Boolean update(Book book);

    Book findById(Long id);

    Boolean insert(Book book);

    List<Book> findByBookName(String bookName);

    Boolean delete(Long id);
}
