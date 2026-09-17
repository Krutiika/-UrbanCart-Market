package com.urbancart.shared.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record InventoryRequest(@NotBlank String productId, @Min(1) int quantity) {}
