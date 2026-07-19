package com.pulseguard.pulseguard.scheduler;


import com.pulseguard.pulseguard.dto.MonitorCheckResponse;
import com.pulseguard.pulseguard.entity.Monitor;
import com.pulseguard.pulseguard.repository.MonitorRepository;
import com.pulseguard.pulseguard.service.MonitorCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "pulseguard.scheduler",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class MonitoringScheduler {

    private final MonitorRepository monitorRepository;
    private final MonitorCheckService monitorCheckService;

    @Scheduled(
            fixedDelayString =
                    "${pulseguard.scheduler.scan-delay-ms:10000}",
            initialDelayString =
                    "${pulseguard.scheduler.initial-delay-ms:5000}"
    )
    public void checkDueMonitors() {

        LocalDateTime currentTime = LocalDateTime.now();

        List<Monitor> activeMonitors =
                monitorRepository.findByActiveTrueOrderByNameAsc();

        if (activeMonitors.isEmpty()) {
            log.debug("No active monitors available");
            return;
        }

        log.info(
                "Scanning {} active monitors",
                activeMonitors.size()
        );

        for (Monitor monitor : activeMonitors) {

            if (!isMonitorDue(monitor, currentTime)) {
                continue;
            }

            try {
                log.info(
                        "Starting automatic check for monitor: {} ({})",
                        monitor.getName(),
                        monitor.getUrl()
                );

                MonitorCheckResponse result =
                        monitorCheckService.checkMonitor(
                                monitor.getId()
                        );

                log.info(
                        "Automatic check completed: monitor={}, " +
                                "status={}, responseTime={} ms",
                        monitor.getName(),
                        result.getStatus(),
                        result.getResponseTimeMs()
                );

            } catch (Exception exception) {

                log.error(
                        "Automatic check failed for monitor id {}: {}",
                        monitor.getId(),
                        exception.getMessage(),
                        exception
                );
            }
        }
    }

    private boolean isMonitorDue(
            Monitor monitor,
            LocalDateTime currentTime) {

        if (monitor.getLastCheckedAt() == null) {
            return true;
        }

        Integer intervalSeconds =
                monitor.getCheckIntervalSeconds();

        if (intervalSeconds == null || intervalSeconds <= 0) {
            intervalSeconds = 60;
        }

        LocalDateTime nextCheckTime =
                monitor.getLastCheckedAt()
                        .plusSeconds(intervalSeconds);

        return !nextCheckTime.isAfter(currentTime);
    }
}

