package com.sjtu.bookstore.dto;

import java.math.BigDecimal;

public class BookSalesStatResponse {

    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private Integer quantity;
    private BigDecimal totalAmount;

    public BookSalesStatResponse(Long bookId, String title, String author, String isbn,
                                 Integer quantity, BigDecimal totalAmount) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
