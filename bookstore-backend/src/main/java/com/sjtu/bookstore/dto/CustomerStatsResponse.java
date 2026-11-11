package com.sjtu.bookstore.dto;

import java.math.BigDecimal;
import java.util.List;

public class CustomerStatsResponse {

    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private List<CustomerBookStatResponse> books;

    public CustomerStatsResponse(Integer totalQuantity, BigDecimal totalAmount,
                                 List<CustomerBookStatResponse> books) {
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.books = books;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<CustomerBookStatResponse> getBooks() {
        return books;
    }
}
