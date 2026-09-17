package com.urbancart.cart.service;

import com.urbancart.cart.model.Cart;
import com.urbancart.cart.model.CartItem;
import com.urbancart.cart.repository.CartRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class CartService {
    private final CartRepository repository;
    public CartService(CartRepository repository) { this.repository = repository; }

    public Cart addItem(String customerId, CartItem item) {
        Cart cart = repository.findByCustomerId(customerId).orElseGet(() -> {
            Cart c = new Cart();
            c.setCustomerId(customerId);
            return c;
        });
        var existing = cart.getItems().stream()
                .filter(current -> current.getProductId().equals(item.getProductId()))
                .findFirst();
        if (existing.isPresent()) {
            var current = existing.get();
            current.setQuantity(Math.max(0, current.getQuantity() + item.getQuantity()));
            if (current.getQuantity() == 0) cart.getItems().remove(current);
        } else if (item.getQuantity() > 0) {
            cart.getItems().add(item);
        }
        cart.setUpdatedAt(Instant.now());
        return repository.save(cart);
    }

    public Cart getCart(String customerId) { return repository.findByCustomerId(customerId).orElseGet(() -> { Cart c = new Cart(); c.setCustomerId(customerId); return c; }); }

    public void clearCart(String customerId) { repository.findByCustomerId(customerId).ifPresent(repository::delete); }

    public Cart removeItem(String customerId, String productId) {
        Cart cart = repository.findByCustomerId(customerId).orElseGet(() -> {
            Cart empty = new Cart();
            empty.setCustomerId(customerId);
            return empty;
        });
        cart.getItems().removeIf(item -> productId.equals(item.getProductId()));
        cart.setUpdatedAt(Instant.now());
        return repository.save(cart);
    }
}
