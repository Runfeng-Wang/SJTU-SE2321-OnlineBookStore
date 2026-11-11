package com.sjtu.bookstore.service;

import com.sjtu.bookstore.dto.LoginRequest;
import com.sjtu.bookstore.dto.RegisterRequest;
import com.sjtu.bookstore.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse register(RegisterRequest request);

    UserResponse login(LoginRequest request);

    List<UserResponse> listUsers();

    UserResponse updateEnabled(Long currentUserId, Long userId, Boolean enabled);
}
