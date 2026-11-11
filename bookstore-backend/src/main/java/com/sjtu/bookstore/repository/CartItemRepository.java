package com.sjtu.bookstore.repository;

import com.sjtu.bookstore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserIdOrderByIdAsc(Long userId);

    Optional<CartItem> findByUserIdAndBookId(Long userId, Long bookId);

    Optional<CartItem> findByIdAndUserId(Long id, Long userId);

    List<CartItem> findByUserIdAndIdIn(Long userId, List<Long> ids);
}
