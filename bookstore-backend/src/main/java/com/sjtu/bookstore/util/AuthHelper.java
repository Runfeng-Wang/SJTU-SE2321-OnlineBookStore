package com.sjtu.bookstore.util;

import com.sjtu.bookstore.dto.CurrentUser;
import com.sjtu.bookstore.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class AuthHelper {

    private AuthHelper() {
    }

    public static CurrentUser requireUser(String authorizationHeader, JwtUtil jwtUtil, UserRepository userRepository) {
        String token = extractBearerToken(authorizationHeader);
        return jwtUtil.parseToken(token, userRepository);
    }

    public static CurrentUser requireAdmin(String authorizationHeader, JwtUtil jwtUtil, UserRepository userRepository) {
        CurrentUser user = requireUser(authorizationHeader, jwtUtil, userRepository);
        if (!"ADMIN".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要管理员权限");
        }
        return user;
    }

    private static String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态无效");
        }
        return authorizationHeader.substring("Bearer ".length()).trim();
    }
}
