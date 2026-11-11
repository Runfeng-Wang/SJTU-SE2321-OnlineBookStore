package com.sjtu.bookstore.controller;

import com.sjtu.bookstore.dto.BookRequest;
import com.sjtu.bookstore.dto.BookResponse;
import com.sjtu.bookstore.repository.UserRepository;
import com.sjtu.bookstore.service.BookService;
import com.sjtu.bookstore.util.AuthHelper;
import com.sjtu.bookstore.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    private final BookService bookService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public BookController(BookService bookService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.bookService = bookService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @GetMapping("/books")
    public List<BookResponse> listBooks(@RequestParam(required = false) String title) {
        return bookService.findAll(title);
    }

    @GetMapping("/book/{id}")
    public BookResponse getBook(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping("/admin/books")
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody BookRequest request) {
        AuthHelper.requireAdmin(authorization, jwtUtil, userRepository);
        return bookService.create(request);
    }

    @PutMapping("/admin/books/{id}")
    public BookResponse updateBook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        AuthHelper.requireAdmin(authorization, jwtUtil, userRepository);
        return bookService.update(id, request);
    }

    @DeleteMapping("/admin/books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id) {
        AuthHelper.requireAdmin(authorization, jwtUtil, userRepository);
        bookService.delete(id);
    }
}
