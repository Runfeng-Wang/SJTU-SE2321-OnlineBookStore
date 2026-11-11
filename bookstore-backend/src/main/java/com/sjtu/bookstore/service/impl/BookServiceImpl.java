package com.sjtu.bookstore.service.impl;

import com.sjtu.bookstore.dto.BookRequest;
import com.sjtu.bookstore.dto.BookResponse;
import com.sjtu.bookstore.entity.Book;
import com.sjtu.bookstore.repository.BookRepository;
import com.sjtu.bookstore.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<BookResponse> findAll(String title) {
        List<Book> books = title == null || title.isBlank()
                ? bookRepository.findAll()
                : bookRepository.findByTitleContainingIgnoreCase(title.trim());
        return books.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BookResponse findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "书籍不存在"));
        return toResponse(book);
    }

    @Override
    public BookResponse create(BookRequest request) {
        Book book = new Book();
        applyRequest(book, request);
        return toResponse(bookRepository.save(book));
    }

    @Override
    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "书籍不存在"));
        applyRequest(book, request);
        return toResponse(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "书籍不存在");
        }
        bookRepository.deleteById(id);
    }

    private void applyRequest(Book book, BookRequest request) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPrice(request.getPrice());
        book.setCategory(request.getCategory());
        book.setImage(request.getImage());
        book.setIsbn(request.getIsbn());
        book.setStock(request.getStock());
        book.setPublisher(request.getPublisher());
        book.setDescription(request.getDescription());
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getCategory(),
                book.getImage(),
                book.getIsbn(),
                book.getStock(),
                book.getPublisher(),
                book.getDescription()
        );
    }
}
