package com.sjtu.bookstore.service.impl;

import com.sjtu.bookstore.dto.BookSalesStatResponse;
import com.sjtu.bookstore.dto.CreateOrderRequest;
import com.sjtu.bookstore.dto.CustomerBookStatResponse;
import com.sjtu.bookstore.dto.CustomerStatsResponse;
import com.sjtu.bookstore.dto.OrderItemResponse;
import com.sjtu.bookstore.dto.OrderResponse;
import com.sjtu.bookstore.dto.UserConsumptionStatResponse;
import com.sjtu.bookstore.entity.Book;
import com.sjtu.bookstore.entity.CartItem;
import com.sjtu.bookstore.entity.CustomerOrder;
import com.sjtu.bookstore.entity.OrderItem;
import com.sjtu.bookstore.entity.User;
import com.sjtu.bookstore.repository.BookRepository;
import com.sjtu.bookstore.repository.CartItemRepository;
import com.sjtu.bookstore.repository.CustomerOrderRepository;
import com.sjtu.bookstore.repository.UserRepository;
import com.sjtu.bookstore.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final CustomerOrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(CustomerOrderRepository orderRepository, CartItemRepository cartItemRepository,
                            BookRepository bookRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(Long userId, LocalDate startDate, LocalDate endDate, String bookName) {
        return filterOrders(loadOrders(userId, startDate, endDate), bookName).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listAllOrders(LocalDate startDate, LocalDate endDate, String bookName) {
        return filterOrders(loadOrders(null, startDate, endDate), bookName).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdAndIdIn(userId, request.getCartItemIds());
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "购物车项无效");
        }
        if (cartItems.size() != request.getCartItemIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "部分购物车项不存在");
        }

        CustomerOrder order = new CustomerOrder();
        order.setUserId(userId);
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Book book = cartItem.getBook();
            int stock = book.getStock() == null ? 0 : book.getStock();
            if (stock < cartItem.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "《" + book.getTitle() + "》库存不足");
            }
            book.setStock(stock - cartItem.getQuantity());
            bookRepository.save(book);

            OrderItem line = new OrderItem();
            line.setOrder(order);
            line.setBookId(book.getId());
            line.setTitle(book.getTitle());
            line.setPrice(book.getPrice());
            line.setImage(book.getImage());
            line.setQuantity(cartItem.getQuantity());
            order.getItems().add(line);
            total = total.add(book.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setTotalAmount(total);
        CustomerOrder saved = orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookSalesStatResponse> getBookSalesStats(LocalDate startDate, LocalDate endDate) {
        Map<Long, BookSalesAccumulator> stats = new HashMap<>();
        for (CustomerOrder order : loadOrders(null, startDate, endDate)) {
            for (OrderItem item : order.getItems()) {
                BookSalesAccumulator accumulator = stats.computeIfAbsent(item.getBookId(), id -> {
                    Optional<Book> book = bookRepository.findById(id);
                    return new BookSalesAccumulator(
                            id,
                            item.getTitle(),
                            book.map(Book::getAuthor).orElse(""),
                            book.map(Book::getIsbn).orElse("")
                    );
                });
                accumulator.quantity += item.getQuantity();
                accumulator.totalAmount = accumulator.totalAmount.add(item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        return stats.values().stream()
                .sorted(Comparator.comparingInt(BookSalesAccumulator::getQuantity).reversed())
                .map(item -> new BookSalesStatResponse(
                        item.bookId,
                        item.title,
                        item.author,
                        item.isbn,
                        item.quantity,
                        item.totalAmount
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserConsumptionStatResponse> getUserConsumptionStats(LocalDate startDate, LocalDate endDate) {
        Map<Long, UserConsumptionAccumulator> stats = new HashMap<>();
        for (CustomerOrder order : loadOrders(null, startDate, endDate)) {
            UserConsumptionAccumulator accumulator = stats.computeIfAbsent(order.getUserId(), id -> {
                Optional<User> user = userRepository.findById(id);
                return new UserConsumptionAccumulator(
                        id,
                        user.map(User::getUsername).orElse("用户" + id),
                        user.map(User::getEmail).orElse("")
                );
            });
            accumulator.totalAmount = accumulator.totalAmount.add(order.getTotalAmount());
            accumulator.totalQuantity += order.getItems().stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();
        }
        return stats.values().stream()
                .sorted(Comparator.comparing(UserConsumptionAccumulator::getTotalAmount).reversed())
                .map(item -> new UserConsumptionStatResponse(
                        item.userId,
                        item.username,
                        item.email,
                        item.totalQuantity,
                        item.totalAmount
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerStatsResponse getCustomerStats(Long userId, LocalDate startDate, LocalDate endDate) {
        Map<Long, CustomerBookAccumulator> stats = new HashMap<>();
        int totalQuantity = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CustomerOrder order : loadOrders(userId, startDate, endDate)) {
            totalAmount = totalAmount.add(order.getTotalAmount());
            for (OrderItem item : order.getItems()) {
                CustomerBookAccumulator accumulator = stats.computeIfAbsent(item.getBookId(),
                        id -> new CustomerBookAccumulator(id, item.getTitle()));
                accumulator.quantity += item.getQuantity();
                accumulator.totalAmount = accumulator.totalAmount.add(item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
                totalQuantity += item.getQuantity();
            }
        }
        List<CustomerBookStatResponse> books = stats.values().stream()
                .sorted(Comparator.comparingInt(CustomerBookAccumulator::getQuantity).reversed())
                .map(item -> new CustomerBookStatResponse(
                        item.bookId,
                        item.title,
                        item.quantity,
                        item.totalAmount
                ))
                .toList();
        return new CustomerStatsResponse(totalQuantity, totalAmount, books);
    }

    private List<CustomerOrder> loadOrders(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return userId == null
                    ? orderRepository.findAllByOrderByCreatedAtDesc()
                    : orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        LocalDateTime start = startDate == null
                ? LocalDate.of(1970, 1, 1).atStartOfDay()
                : startDate.atStartOfDay();
        LocalDateTime end = endDate == null
                ? LocalDateTime.now().plusYears(100)
                : endDate.plusDays(1).atStartOfDay().minusNanos(1);
        return userId == null
                ? orderRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end)
                : orderRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, start, end);
    }

    private List<CustomerOrder> filterOrders(List<CustomerOrder> orders, String bookName) {
        if (bookName == null || bookName.isBlank()) {
            return orders;
        }
        String keyword = bookName.trim().toLowerCase();
        return orders.stream()
                .filter(order -> order.getItems().stream()
                        .anyMatch(item -> item.getTitle().toLowerCase().contains(keyword)))
                .toList();
    }

    private OrderResponse toResponse(CustomerOrder order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getBookId(),
                        item.getTitle(),
                        item.getPrice(),
                        item.getImage(),
                        item.getQuantity()))
                .toList();
        Optional<User> user = userRepository.findById(order.getUserId());
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                user.map(User::getUsername).orElse("用户" + order.getUserId()),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }

    private static final class BookSalesAccumulator {
        private final Long bookId;
        private final String title;
        private final String author;
        private final String isbn;
        private Integer quantity = 0;
        private BigDecimal totalAmount = BigDecimal.ZERO;

        private BookSalesAccumulator(Long bookId, String title, String author, String isbn) {
            this.bookId = bookId;
            this.title = title;
            this.author = author;
            this.isbn = isbn;
        }

        private Integer getQuantity() {
            return quantity;
        }
    }

    private static final class UserConsumptionAccumulator {
        private final Long userId;
        private final String username;
        private final String email;
        private Integer totalQuantity = 0;
        private BigDecimal totalAmount = BigDecimal.ZERO;

        private UserConsumptionAccumulator(Long userId, String username, String email) {
            this.userId = userId;
            this.username = username;
            this.email = email;
        }

        private BigDecimal getTotalAmount() {
            return totalAmount;
        }
    }

    private static final class CustomerBookAccumulator {
        private final Long bookId;
        private final String title;
        private Integer quantity = 0;
        private BigDecimal totalAmount = BigDecimal.ZERO;

        private CustomerBookAccumulator(Long bookId, String title) {
            this.bookId = bookId;
            this.title = title;
        }

        private Integer getQuantity() {
            return quantity;
        }
    }
}
