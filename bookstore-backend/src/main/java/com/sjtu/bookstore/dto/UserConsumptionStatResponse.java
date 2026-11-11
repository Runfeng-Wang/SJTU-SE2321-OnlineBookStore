package com.sjtu.bookstore.dto;

import java.math.BigDecimal;

public class UserConsumptionStatResponse {

    private Long userId;
    private String username;
    private String email;
    private Integer totalQuantity;
    private BigDecimal totalAmount;

    public UserConsumptionStatResponse(Long userId, String username, String email,
                                       Integer totalQuantity, BigDecimal totalAmount) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
