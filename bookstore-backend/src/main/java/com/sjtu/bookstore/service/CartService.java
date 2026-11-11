package com.sjtu.bookstore.service;

import com.sjtu.bookstore.dto.AddCartRequest;
import com.sjtu.bookstore.dto.CartItemResponse;

import java.util.List;

public interface CartService {

    List<CartItemResponse> listCart(Long userId);

    CartItemResponse addToCart(Long userId, AddCartRequest request);

    void removeCartItem(Long userId, Long cartItemId);
}
