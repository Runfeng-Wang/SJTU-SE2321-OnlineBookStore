package com.sjtu.bookstore.dto;

import java.math.BigDecimal;

public class CartItemResponse {

    private Long cartId;
    private Long bookId;
    private String title;
    private String author;
    private BigDecimal price;
    private String image;
    private Integer quantity;

    public CartItemResponse(Long cartId, Long bookId, String title, String author,
                            BigDecimal price, String image, Integer quantity) {
        this.cartId = cartId;
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
