package com.pulseguard.pulseguard.service;


import com.pulseguard.pulseguard.dto.MonitorStatisticsResponse;
import com.pulseguard.pulseguard.entity.Monitor;
import com.pulseguard.pulseguard.entity.MonitorResult;
import com.pulseguard.pulseguard.enums.MonitorStatus;
import com.pulseguard.pulseguard.exception.MonitorNotFoundException;
import com.pulseguard.pulseguard.repository.MonitorRepository;
import com.pulseguard.pulseguard.repository.MonitorResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitorStatisticsService {

    private final MonitorRepository monitorRepository;
    private final MonitorResultRepository monitorResultRepository;

    @Transactional(readOnly = true)
    public MonitorStatisticsResponse getStatistics(Long monitorId) {

        Monitor monitor = monitorRepository
                .findById(monitorId)
                .orElseThrow(() ->
                        new MonitorNotFoundException(monitorId)
                );

        List<MonitorResult> results =
                monitorResultRepository
                        .findByMonitorIdOrderByCheckedAtDesc(monitorId);

        long totalChecks = results.size();

        long successfulChecks = results.stream()
                .filter(result ->
                        result.getStatus() == MonitorStatus.UP
                )
                .count();

        long failedChecks = results.stream()
                .filter(result ->
                        result.getStatus() == MonitorStatus.DOWN
                )
                .count();

        double uptimePercentage = totalChecks == 0
                ? 0.0
                : round(
                successfulChecks * 100.0 / totalChecks
        );

        double averageResponseTime = results.stream()
                .mapToLong(MonitorResult::getResponseTimeMs)
                .average()
                .orElse(0.0);

        Long minimumResponseTime = results.stream()
                .map(MonitorResult::getResponseTimeMs)
                .min(Long::compareTo)
                .orElse(null);

        Long maximumResponseTime = results.stream()
                .map(MonitorResult::getResponseTimeMs)
                .max(Long::compareTo)
                .orElse(null);

        LocalDateTime lastCheckedAt = results.isEmpty()
                ? null
                : results.get(0).getCheckedAt();

        return MonitorStatisticsResponse.builder()
                .monitorId(monitor.getId())
                .monitorName(monitor.getName())
                .url(monitor.getUrl())
                .currentStatus(monitor.getCurrentStatus())
                .totalChecks(totalChecks)
                .successfulChecks(successfulChecks)
                .failedChecks(failedChecks)
                .uptimePercentage(uptimePercentage)
                .averageResponseTimeMs(
                        round(averageResponseTime)
                )
                .minimumResponseTimeMs(minimumResponseTime)
                .maximumResponseTimeMs(maximumResponseTime)
                .lastCheckedAt(lastCheckedAt)
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
