package com.sjtu.bookstore.util;

import com.sjtu.bookstore.dto.CurrentUser;
import com.sjtu.bookstore.entity.User;
import com.sjtu.bookstore.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtUtil {

    private static final Pattern USER_ID_PATTERN = Pattern.compile("\"sub\":(\\d+)");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("\"username\":\"([^\"]+)\"");
    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\":(\\d+)");

    @Value("${app.jwt.secret:bookstore-dev-secret-change-me}")
    private String secret;

    @Value("${app.jwt.expiration-seconds:86400}")
    private long expirationSeconds;

    private byte[] secretBytes;

    @PostConstruct
    void init() {
        secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    }

    public String generateToken(User user) {
        long exp = Instant.now().getEpochSecond() + expirationSeconds;
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"sub\":" + user.getId()
                + ",\"username\":\"" + escapeJson(user.getUsername())
                + "\",\"exp\":" + exp + "}";
        String header = encode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = encode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signature = sign(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public CurrentUser parseToken(String token, UserRepository userRepository) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态无效");
        }
        String expectedSignature = sign(parts[0] + "." + parts[1]);
        if (!constantTimeEquals(expectedSignature, parts[2])) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态无效");
        }
        String payload = decodeToString(parts[1]);
        Long userId = extractLong(payload, USER_ID_PATTERN, "登录状态无效");
        long exp = extractLong(payload, EXP_PATTERN, "登录状态无效");
        if (Instant.now().getEpochSecond() >= exp) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录已过期，请重新登录");
        }
        String username = extractString(payload, USERNAME_PATTERN, "登录状态无效");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户不存在"));
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "您的账号已经被禁用");
        }
        String role = user.getRole() == null ? "CUSTOMER" : user.getRole();
        Boolean enabled = user.getEnabled() == null ? true : user.getEnabled();
        return new CurrentUser(userId, username, role, enabled);
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"));
            return encode(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("JWT 签名失败", ex);
        }
    }

    private String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String decodeToString(String value) {
        try {
            return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录状态无效");
        }
    }

    private Long extractLong(String text, Pattern pattern, String message) {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
        }
        return Long.parseLong(matcher.group(1));
    }

    private String extractString(String text, Pattern pattern, String message) {
        Matcher matcher = pattern.matcher(text);
        if (!matcher.find()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
        }
        return matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private boolean constantTimeEquals(String a, String b) {
        return a.length() == b.length() && MessageDigestSupport.isEqual(a, b);
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static final class MessageDigestSupport {
        private static boolean isEqual(String a, String b) {
            int result = 0;
            for (int i = 0; i < a.length(); i++) {
                result |= a.charAt(i) ^ b.charAt(i);
            }
            return result == 0;
        }
    }
}
