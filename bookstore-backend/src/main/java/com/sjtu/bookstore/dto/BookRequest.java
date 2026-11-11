package com.sjtu.bookstore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class BookRequest {

    @NotBlank(message = "书名不能为空")
    @Size(max = 128, message = "书名不能超过 128 个字符")
    private String title;

    @NotBlank(message = "作者不能为空")
    @Size(max = 128, message = "作者不能超过 128 个字符")
    private String author;

    @NotNull(message = "定价不能为空")
    @DecimalMin(value = "0.00", message = "定价不能为负数")
    private BigDecimal price;

    @Size(max = 64, message = "分类不能超过 64 个字符")
    private String category;

    @Size(max = 255, message = "封面地址不能超过 255 个字符")
    private String image;

    @Size(max = 32, message = "ISBN 不能超过 32 个字符")
    private String isbn;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    @Size(max = 128, message = "出版社不能超过 128 个字符")
    private String publisher;

    private String description;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
