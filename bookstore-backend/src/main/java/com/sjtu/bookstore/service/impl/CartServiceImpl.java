package com.sjtu.bookstore.service.impl;

import com.sjtu.bookstore.dto.AddCartRequest;
import com.sjtu.bookstore.dto.CartItemResponse;
import com.sjtu.bookstore.entity.Book;
import com.sjtu.bookstore.entity.CartItem;
import com.sjtu.bookstore.repository.BookRepository;
import com.sjtu.bookstore.repository.CartItemRepository;
import com.sjtu.bookstore.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public CartServiceImpl(CartItemRepository cartItemRepository, BookRepository bookRepository) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<CartItemResponse> listCart(Long userId) {
        return cartItemRepository.findByUserIdOrderByIdAsc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CartItemResponse addToCart(Long userId, AddCartRequest request) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "书籍不存在"));

        CartItem item = cartItemRepository.findByUserIdAndBookId(userId, book.getId())
                .orElseGet(() -> {
                    CartItem created = new CartItem();
                    created.setUserId(userId);
                    created.setBook(book);
                    created.setQuantity(0);
                    return created;
                });
        int stock = book.getStock() == null ? 0 : book.getStock();
        if (item.getQuantity() + 1 > stock) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "库存不足");
        }
        item.setQuantity(item.getQuantity() + 1);
        CartItem saved = cartItemRepository.save(item);
        return toResponse(saved);
    }

    @Override
    public void removeCartItem(Long userId, Long cartItemId) {
        CartItem item = cartItemRepository.findByIdAndUserId(cartItemId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "购物车项不存在"));
        cartItemRepository.delete(item);
    }

    private CartItemResponse toResponse(CartItem item) {
        Book book = item.getBook();
        return new CartItemResponse(
                item.getId(),
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getImage(),
                item.getQuantity()
        );
    }
}
