package com.urbancart.inventory.controller;

import com.urbancart.inventory.model.Inventory;
import com.urbancart.inventory.service.InventoryService;
import com.urbancart.shared.dto.ApiResponse;
import com.urbancart.shared.dto.InventoryRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService service;
    public InventoryController(InventoryService service) { this.service = service; }

    @PostMapping
    public ApiResponse<Inventory> create(@RequestBody Inventory inventory) { return ApiResponse.success("Inventory created", service.create(inventory)); }
    @GetMapping("/{productId}")
    public ApiResponse<Inventory> get(@PathVariable String productId) { return ApiResponse.success("Inventory fetched", service.findByProductId(productId)); }
    @PostMapping("/reserve")
    public ApiResponse<Inventory> reserve(@Valid @RequestBody InventoryRequest request) { return ApiResponse.success("Inventory reserved", service.reserve(request)); }
    @PostMapping("/release")
    public ApiResponse<Inventory> release(@Valid @RequestBody InventoryRequest request) { return ApiResponse.success("Inventory released", service.release(request)); }
}
