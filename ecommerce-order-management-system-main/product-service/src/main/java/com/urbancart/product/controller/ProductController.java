package com.urbancart.product.controller;

import com.urbancart.product.model.Product;
import com.urbancart.product.service.ProductService;
import com.urbancart.shared.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    @PostMapping
    public ApiResponse<Product> create(@RequestBody Product product) { return ApiResponse.success("Product created", service.create(product)); }
    @GetMapping("/{id}")
    public ApiResponse<Product> get(@PathVariable String id) { return ApiResponse.success("Product found", service.findById(id)); }
    @GetMapping("/search")
    public ApiResponse<List<Product>> search(@RequestParam String keyword) { return ApiResponse.success("Search result", service.search(keyword)); }
    @GetMapping("/category/{category}")
    public ApiResponse<List<Product>> category(@PathVariable String category) { return ApiResponse.success("Category result", service.findByCategory(category)); }
}
