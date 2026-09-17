package com.urbancart.notification.controller;

import com.urbancart.shared.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    @GetMapping("/health-check")
    public ApiResponse<String> health() {
        return ApiResponse.success("Notification service is running", "OK");
    }
}
