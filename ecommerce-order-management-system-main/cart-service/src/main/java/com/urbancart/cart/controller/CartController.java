package com.urbancart.cart.controller;

import com.urbancart.cart.model.Cart;
import com.urbancart.cart.model.CartItem;
import com.urbancart.cart.service.CartService;
import com.urbancart.shared.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService service;
    public CartController(CartService service) { this.service = service; }

    @PostMapping("/{customerId}/items")
    public ApiResponse<Cart> addItem(@PathVariable String customerId, @RequestBody CartItem item) { return ApiResponse.success("Item added", service.addItem(customerId, item)); }
    @GetMapping("/{customerId}")
    public ApiResponse<Cart> getCart(@PathVariable String customerId) { return ApiResponse.success("Cart fetched", service.getCart(customerId)); }
    @DeleteMapping("/{customerId}")
    public ApiResponse<Void> clear(@PathVariable String customerId) { service.clearCart(customerId); return ApiResponse.success("Cart cleared", null); }

    @DeleteMapping("/{customerId}/items/{productId}")
    public ApiResponse<Cart> removeItem(@PathVariable String customerId, @PathVariable String productId) {
        return ApiResponse.success("Item removed", service.removeItem(customerId, productId));
    }
}
