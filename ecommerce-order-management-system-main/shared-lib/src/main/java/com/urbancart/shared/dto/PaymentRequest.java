package com.urbancart.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PaymentRequest(@NotBlank String orderId, @NotNull BigDecimal amount, @NotBlank String paymentMode) {}
