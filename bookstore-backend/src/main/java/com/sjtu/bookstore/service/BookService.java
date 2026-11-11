package com.sjtu.bookstore.service;

import com.sjtu.bookstore.dto.BookResponse;
import com.sjtu.bookstore.dto.BookRequest;

import java.util.List;

public interface BookService {

    List<BookResponse> findAll(String title);

    BookResponse findById(Long id);

    BookResponse create(BookRequest request);

    BookResponse update(Long id, BookRequest request);

    void delete(Long id);
}
