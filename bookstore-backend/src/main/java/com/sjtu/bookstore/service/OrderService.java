package com.sjtu.bookstore.service;

import com.sjtu.bookstore.dto.BookSalesStatResponse;
import com.sjtu.bookstore.dto.CreateOrderRequest;
import com.sjtu.bookstore.dto.CustomerStatsResponse;
import com.sjtu.bookstore.dto.OrderResponse;
import com.sjtu.bookstore.dto.UserConsumptionStatResponse;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    List<OrderResponse> listOrders(Long userId, LocalDate startDate, LocalDate endDate, String bookName);

    List<OrderResponse> listAllOrders(LocalDate startDate, LocalDate endDate, String bookName);

    OrderResponse createOrder(Long userId, CreateOrderRequest request);

    List<BookSalesStatResponse> getBookSalesStats(LocalDate startDate, LocalDate endDate);

    List<UserConsumptionStatResponse> getUserConsumptionStats(LocalDate startDate, LocalDate endDate);

    CustomerStatsResponse getCustomerStats(Long userId, LocalDate startDate, LocalDate endDate);
}
