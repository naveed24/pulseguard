package com.pulseguard.pulseguard.controller;


import com.pulseguard.pulseguard.dto.MonitorCheckResponse;
import com.pulseguard.pulseguard.dto.MonitorRequest;
import com.pulseguard.pulseguard.dto.MonitorResponse;
import com.pulseguard.pulseguard.dto.MonitorResultResponse;
import com.pulseguard.pulseguard.service.MonitorCheckService;
import com.pulseguard.pulseguard.service.MonitorHistoryService;
import com.pulseguard.pulseguard.service.MonitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitors")
@RequiredArgsConstructor
public class MonitorController {

    private final MonitorService monitorService;

    private final MonitorCheckService monitorCheckService;

    private final MonitorHistoryService monitorHistoryService;

    @PostMapping
    public ResponseEntity<MonitorResponse> createMonitor(
            @Valid @RequestBody MonitorRequest request) {

        MonitorResponse response =
                monitorService.createMonitor(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<MonitorResponse>> getAllMonitors() {

        return ResponseEntity.ok(
                monitorService.getAllMonitors()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonitorResponse> getMonitorById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                monitorService.getMonitorById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MonitorResponse> updateMonitor(
            @PathVariable Long id,
            @Valid @RequestBody MonitorRequest request) {

        return ResponseEntity.ok(
                monitorService.updateMonitor(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonitor(
            @PathVariable Long id) {

        monitorService.deleteMonitor(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/check")
    public ResponseEntity<MonitorCheckResponse> checkMonitor(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                monitorCheckService.checkMonitor(id)
        );
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<MonitorResultResponse>>
    getMonitorHistory(@PathVariable Long id) {

        return ResponseEntity.ok(
                monitorHistoryService.getMonitorHistory(id)
        );
    }



}
