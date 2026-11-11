package com.sjtu.bookstore.dto;

import java.math.BigDecimal;

public class CustomerBookStatResponse {

    private Long bookId;
    private String title;
    private Integer quantity;
    private BigDecimal totalAmount;

    public CustomerBookStatResponse(Long bookId, String title, Integer quantity, BigDecimal totalAmount) {
        this.bookId = bookId;
        this.title = title;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
