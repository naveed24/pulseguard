package com.pulseguard.pulseguard.service;


import com.pulseguard.pulseguard.dto.MonitorCheckResponse;
import com.pulseguard.pulseguard.entity.Monitor;
import com.pulseguard.pulseguard.entity.MonitorResult;
import com.pulseguard.pulseguard.enums.MonitorStatus;
import com.pulseguard.pulseguard.exception.MonitorNotFoundException;
import com.pulseguard.pulseguard.repository.MonitorRepository;
import com.pulseguard.pulseguard.repository.MonitorResultRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;



@Service
@RequiredArgsConstructor
public class MonitorCheckService {

    private final MonitorRepository monitorRepository;

    private final MonitorResultRepository monitorResultRepository;
    private final HttpClient httpClient;


    @Transactional
    public MonitorCheckResponse checkMonitor(Long monitorId) {

        Monitor monitor = monitorRepository
                .findById(monitorId)
                .orElseThrow(() ->
                        new MonitorNotFoundException(monitorId)
                );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(monitor.getUrl()))
                .timeout(Duration.ofSeconds(15))
                .header(
                        "User-Agent",
                        "PulseGuard-Uptime-Monitor/1.0"
                )
                .GET()
                .build();

        long startTime = System.nanoTime();

        try {
            HttpResponse<Void> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );

            long responseTimeMs = calculateResponseTime(startTime);
            int actualStatusCode = response.statusCode();

            MonitorStatus status =
                    actualStatusCode ==
                            monitor.getExpectedStatusCode()
                            ? MonitorStatus.UP
                            : MonitorStatus.DOWN;

            LocalDateTime checkedAt = LocalDateTime.now();

            saveCheckResult(
                    monitor,
                    status,
                    actualStatusCode,
                    responseTimeMs,
                    null,
                    checkedAt
            );

            String message = status == MonitorStatus.UP
                    ? "Website is available"
                    : "Website returned an unexpected status code";

            return MonitorCheckResponse.builder()
                    .monitorId(monitor.getId())
                    .monitorName(monitor.getName())
                    .url(monitor.getUrl())
                    .status(status)
                    .expectedStatusCode(
                            monitor.getExpectedStatusCode()
                    )
                    .actualStatusCode(actualStatusCode)
                    .responseTimeMs(responseTimeMs)
                    .message(message)
                    .errorMessage(null)
                    .checkedAt(checkedAt)
                    .build();

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            return handleFailedCheck(
                    monitor,
                    startTime,
                    "Website check was interrupted"
            );

        } catch (IOException | RuntimeException exception) {

            return handleFailedCheck(
                    monitor,
                    startTime,
                    exception.getMessage()
            );
        }
    }

    private MonitorCheckResponse handleFailedCheck(
            Monitor monitor,
            long startTime,
            String errorMessage) {

        long responseTimeMs = calculateResponseTime(startTime);
        LocalDateTime checkedAt = LocalDateTime.now();

        saveCheckResult(
                monitor,
                MonitorStatus.DOWN,
                null,
                responseTimeMs,
                errorMessage,
                checkedAt
        );

        return MonitorCheckResponse.builder()
                .monitorId(monitor.getId())
                .monitorName(monitor.getName())
                .url(monitor.getUrl())
                .status(MonitorStatus.DOWN)
                .expectedStatusCode(
                        monitor.getExpectedStatusCode()
                )
                .actualStatusCode(null)
                .responseTimeMs(responseTimeMs)
                .message("Website is unavailable")
                .errorMessage(errorMessage)
                .checkedAt(checkedAt)
                .build();
    }


    private void saveCheckResult(
            Monitor monitor,
            MonitorStatus status,
            Integer actualStatusCode,
            Long responseTimeMs,
            String errorMessage,
            LocalDateTime checkedAt) {

        monitor.setCurrentStatus(status);
        monitor.setLastStatusCode(actualStatusCode);
        monitor.setLastResponseTimeMs(responseTimeMs);
        monitor.setLastCheckedAt(checkedAt);

        monitorRepository.save(monitor);

        MonitorResult result = new MonitorResult();

        result.setMonitor(monitor);
        result.setStatus(status);
        result.setExpectedStatusCode(
                monitor.getExpectedStatusCode()
        );
        result.setActualStatusCode(actualStatusCode);
        result.setResponseTimeMs(responseTimeMs);
        result.setErrorMessage(errorMessage);
        result.setCheckedAt(checkedAt);

        monitorResultRepository.save(result);
    }

    private long calculateResponseTime(long startTime) {

        long elapsedNanoseconds = System.nanoTime() - startTime;

        return elapsedNanoseconds / 1_000_000;
    }
}
