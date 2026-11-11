package com.sjtu.bookstore.controller;

import com.sjtu.bookstore.dto.BookSalesStatResponse;
import com.sjtu.bookstore.dto.CreateOrderRequest;
import com.sjtu.bookstore.dto.CurrentUser;
import com.sjtu.bookstore.dto.CustomerStatsResponse;
import com.sjtu.bookstore.dto.OrderResponse;
import com.sjtu.bookstore.dto.UserConsumptionStatResponse;
import com.sjtu.bookstore.repository.UserRepository;
import com.sjtu.bookstore.service.OrderService;
import com.sjtu.bookstore.util.AuthHelper;
import com.sjtu.bookstore.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<OrderResponse> listOrders(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String bookName) {
        CurrentUser user = AuthHelper.requireUser(authorization, jwtUtil, userRepository);
        return orderService.listOrders(user.getId(), startDate, endDate, bookName);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @Valid @RequestBody CreateOrderRequest request) {
        CurrentUser user = AuthHelper.requireUser(authorization, jwtUtil, userRepository);
        return orderService.createOrder(user.getId(), request);
    }

    @GetMapping("/admin")
    public List<OrderResponse> listAllOrders(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String bookName) {
        AuthHelper.requireAdmin(authorization, jwtUtil, userRepository);
        return orderService.listAllOrders(startDate, endDate, bookName);
    }

    @GetMapping("/stats/books")
    public List<BookSalesStatResponse> getBookSalesStats(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        AuthHelper.requireAdmin(authorization, jwtUtil, userRepository);
        return orderService.getBookSalesStats(startDate, endDate);
    }

    @GetMapping("/stats/users")
    public List<UserConsumptionStatResponse> getUserConsumptionStats(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        AuthHelper.requireAdmin(authorization, jwtUtil, userRepository);
        return orderService.getUserConsumptionStats(startDate, endDate);
    }

    @GetMapping("/stats/me")
    public CustomerStatsResponse getMyStats(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        CurrentUser user = AuthHelper.requireUser(authorization, jwtUtil, userRepository);
        return orderService.getCustomerStats(user.getId(), startDate, endDate);
    }
}
