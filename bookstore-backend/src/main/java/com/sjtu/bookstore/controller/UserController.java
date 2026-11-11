package com.sjtu.bookstore.controller;

import com.sjtu.bookstore.dto.LoginRequest;
import com.sjtu.bookstore.dto.CurrentUser;
import com.sjtu.bookstore.dto.RegisterRequest;
import com.sjtu.bookstore.dto.UserStatusRequest;
import com.sjtu.bookstore.dto.UserResponse;
import com.sjtu.bookstore.repository.UserRepository;
import com.sjtu.bookstore.service.UserService;
import com.sjtu.bookstore.util.AuthHelper;
import com.sjtu.bookstore.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public UserController(UserService userService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @GetMapping
    public List<UserResponse> listUsers(@RequestHeader(value = "Authorization", required = false) String authorization) {
        AuthHelper.requireAdmin(authorization, jwtUtil, userRepository);
        return userService.listUsers();
    }

    @PatchMapping("/{id}/status")
    public UserResponse updateStatus(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @PathVariable Long id,
            @Valid @RequestBody UserStatusRequest request) {
        CurrentUser user = AuthHelper.requireAdmin(authorization, jwtUtil, userRepository);
        return userService.updateEnabled(user.getId(), id, request.getEnabled());
    }
}
