package com.sjtu.bookstore.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long id;
    private Long bookId;
    private String title;
    private BigDecimal price;
    private String image;
    private Integer quantity;

    public OrderItemResponse(Long id, Long bookId, String title, BigDecimal price, String image, Integer quantity) {
        this.id = id;
        this.bookId = bookId;
        this.title = title;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
