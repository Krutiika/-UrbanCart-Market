package com.urbancart.product.config;

import com.urbancart.product.model.Product;
import com.urbancart.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class ProductDataInitializer {
    @Bean
    CommandLineRunner seedProducts(ProductRepository repository) {
        return args -> {
            if (repository.count() > 0) return;
            repository.saveAll(List.of(
                    product("aero-wireless-pro", "AeroWireless Pro", "Audio", "2499"),
                    product("urbanfit-watch", "UrbanFit Watch", "Wearables", "3999"),
                    product("lumabook-14", "LumaBook 14", "Laptops", "75999"),
                    product("pixelnest-speaker", "PixelNest Speaker", "Home", "1699")
            ));
        };
    }

    private Product product(String id, String name, String category, String price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setCategory(category);
        product.setPrice(new BigDecimal(price));
        product.setAvailable(true);
        return product;
    }
}