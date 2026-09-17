package com.urbancart.shipping.controller;

import com.urbancart.shared.dto.ApiResponse;
import com.urbancart.shipping.model.Shipment;
import com.urbancart.shipping.service.ShippingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shipments")
public class ShippingController {
    private final ShippingService service;
    public ShippingController(ShippingService service) { this.service = service; }

    @GetMapping("/order/{orderId}")
    public ApiResponse<Shipment> getByOrder(@PathVariable String orderId) { return ApiResponse.success("Shipment fetched", service.findByOrderId(orderId)); }
}
