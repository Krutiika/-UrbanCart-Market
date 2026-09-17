package com.urbancart.shared.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank String customerId,
        @NotBlank String productId,
        @Min(1) int quantity,
        @NotNull @DecimalMin("1.0") BigDecimal amount,
        @NotBlank String paymentMode,
        @NotBlank String deliveryAddress
) {}
