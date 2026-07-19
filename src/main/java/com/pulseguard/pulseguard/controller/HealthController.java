package com.pulseguard.pulseguard.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("application", "PulseGuard");
        response.put("status", "UP");
        response.put("message", "PulseGuard is running successfully");
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response);
    }
}
