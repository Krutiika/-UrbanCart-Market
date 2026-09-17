package com.urbancart.product.service;

import com.urbancart.product.model.Product;
import com.urbancart.product.repository.ProductRepository;
import com.urbancart.shared.exception.BusinessException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;
    public ProductService(ProductRepository repository) { this.repository = repository; }
    public Product create(Product product) { return repository.save(product); }
    public Product findById(String id) {
        Product product = repository.findById(id).orElseThrow(() -> new BusinessException("Product not found: " + id));
        initializeAttributes(product);
        return product;
    }
    public List<Product> search(String keyword) {
        List<Product> products = repository.findByNameContainingIgnoreCase(keyword);
        products.forEach(this::initializeAttributes);
        return products;
    }
    public List<Product> findByCategory(String category) {
        List<Product> products = repository.findByCategoryIgnoreCase(category);
        products.forEach(this::initializeAttributes);
        return products;
    }

    private void initializeAttributes(Product product) {
        if (product.getAttributes() != null) {
            product.getAttributes().size();
        }
    }
}
