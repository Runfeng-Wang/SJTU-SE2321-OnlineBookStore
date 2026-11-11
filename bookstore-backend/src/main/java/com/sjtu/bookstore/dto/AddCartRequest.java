package com.sjtu.bookstore.dto;

import jakarta.validation.constraints.NotNull;

public class AddCartRequest {

    @NotNull(message = "书籍 ID 不能为空")
    private Long bookId;

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
}
