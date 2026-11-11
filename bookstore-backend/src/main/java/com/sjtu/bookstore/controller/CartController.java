package com.sjtu.bookstore.controller;

import com.sjtu.bookstore.dto.AddCartRequest;
import com.sjtu.bookstore.dto.CartItemResponse;
import com.sjtu.bookstore.dto.CurrentUser;
import com.sjtu.bookstore.repository.UserRepository;
import com.sjtu.bookstore.service.CartService;
import com.sjtu.bookstore.util.AuthHelper;
import com.sjtu.bookstore.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public CartController(CartService cartService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.cartService = cartService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<CartItemResponse> listCart(@RequestHeader(value = "Authorization", required = false) String authorization) {
        CurrentUser user = AuthHelper.requireUser(authorization, jwtUtil, userRepository);
        return cartService.listCart(user.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse addToCart(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody AddCartRequest request) {
        CurrentUser user = AuthHelper.requireUser(authorization, jwtUtil, userRepository);
        return cartService.addToCart(user.getId(), request);
    }

    @DeleteMapping("/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCartItem(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long cartItemId) {
        CurrentUser user = AuthHelper.requireUser(authorization, jwtUtil, userRepository);
        cartService.removeCartItem(user.getId(), cartItemId);
    }
}
