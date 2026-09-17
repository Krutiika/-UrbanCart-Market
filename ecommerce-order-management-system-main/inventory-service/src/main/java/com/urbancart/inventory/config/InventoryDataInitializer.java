package com.urbancart.inventory.config;

import com.urbancart.inventory.model.Inventory;
import com.urbancart.inventory.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InventoryDataInitializer {
    @Bean
    CommandLineRunner seedInventory(InventoryRepository repository) {
        return args -> {
            List.of(
                    inventory("aero-wireless-pro", 25),
                    inventory("urbanfit-watch", 25),
                    inventory("lumabook-14", 10),
                    inventory("pixelnest-speaker", 25)
            ).forEach(seed -> repository.findByProductId(seed.getProductId()).orElseGet(() -> repository.save(seed)));
        };
    }

    private Inventory inventory(String productId, int availableQuantity) {
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setAvailableQuantity(availableQuantity);
        inventory.setReservedQuantity(0);
        return inventory;
    }
}