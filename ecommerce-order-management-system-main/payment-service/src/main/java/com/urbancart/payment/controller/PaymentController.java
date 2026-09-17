package com.urbancart.payment.controller;

import com.urbancart.payment.model.PaymentTransaction;
import com.urbancart.payment.service.PaymentService;
import com.urbancart.shared.dto.ApiResponse;
import com.urbancart.shared.dto.PaymentRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }

    @PostMapping("/process")
    public ApiResponse<PaymentTransaction> process(@Valid @RequestBody PaymentRequest request) {
        return ApiResponse.success("Payment processed", service.process(request));
    }
}
