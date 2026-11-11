package com.sjtu.bookstore.service.impl;

import com.sjtu.bookstore.dto.LoginRequest;
import com.sjtu.bookstore.dto.RegisterRequest;
import com.sjtu.bookstore.dto.UserResponse;
import com.sjtu.bookstore.entity.User;
import com.sjtu.bookstore.repository.UserRepository;
import com.sjtu.bookstore.service.UserService;
import com.sjtu.bookstore.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "两次输入的密码不一致");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "邮箱已被注册");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRole("CUSTOMER");
        user.setEnabled(true);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    @Override
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误"));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "您的账号已经被禁用");
        }
        return toResponse(user);
    }

    @Override
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        normalizeRole(user),
                        normalizeEnabled(user),
                        null
                ))
                .toList();
    }

    @Override
    public UserResponse updateEnabled(Long currentUserId, Long userId, Boolean enabled) {
        if (currentUserId.equals(userId) && !Boolean.TRUE.equals(enabled)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能禁用当前登录的管理员账号");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        user.setEnabled(enabled);
        User saved = userRepository.save(user);
        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                normalizeRole(saved),
                normalizeEnabled(saved),
                null
        );
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                normalizeRole(user),
                normalizeEnabled(user),
                jwtUtil.generateToken(user)
        );
    }

    private String normalizeRole(User user) {
        return user.getRole() == null ? "CUSTOMER" : user.getRole();
    }

    private Boolean normalizeEnabled(User user) {
        return user.getEnabled() == null ? true : user.getEnabled();
    }
}
