package com.pulseguard.pulseguard.service;


import com.pulseguard.pulseguard.dto.MonitorRequest;
import com.pulseguard.pulseguard.dto.MonitorResponse;
import com.pulseguard.pulseguard.entity.Monitor;
import com.pulseguard.pulseguard.enums.MonitorStatus;
import com.pulseguard.pulseguard.exception.DuplicateMonitorException;
import com.pulseguard.pulseguard.exception.InvalidUrlException;
import com.pulseguard.pulseguard.exception.MonitorNotFoundException;
import com.pulseguard.pulseguard.repository.MonitorRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


//


@Service
@RequiredArgsConstructor
public class MonitorService {

    private final MonitorRepository monitorRepository;

    @Transactional
    public MonitorResponse createMonitor(MonitorRequest request) {

        String normalizedUrl = validateAndNormalizeUrl(request.getUrl());

        if (monitorRepository.existsByUrlIgnoreCase(normalizedUrl)) {
            throw new DuplicateMonitorException(normalizedUrl);
        }

        Monitor monitor = new Monitor();

        monitor.setName(request.getName().trim());
        monitor.setUrl(normalizedUrl);
        monitor.setExpectedStatusCode(
                request.getExpectedStatusCode() != null
                        ? request.getExpectedStatusCode()
                        : 200
        );
        monitor.setCheckIntervalSeconds(
                request.getCheckIntervalSeconds() != null
                        ? request.getCheckIntervalSeconds()
                        : 60
        );
        monitor.setActive(
                request.getActive() != null
                        ? request.getActive()
                        : true
        );
        monitor.setCurrentStatus(MonitorStatus.UNKNOWN);

        Monitor savedMonitor = monitorRepository.save(monitor);

        return mapToResponse(savedMonitor);
    }

    @Transactional(readOnly = true)
    public List<MonitorResponse> getAllMonitors() {

        return monitorRepository
                .findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MonitorResponse getMonitorById(Long id) {

        Monitor monitor = findMonitor(id);

        return mapToResponse(monitor);
    }

    @Transactional
    public MonitorResponse updateMonitor(
            Long id,
            MonitorRequest request) {

        Monitor monitor = findMonitor(id);

        String normalizedUrl = validateAndNormalizeUrl(request.getUrl());

        boolean urlWasChanged =
                !monitor.getUrl().equalsIgnoreCase(normalizedUrl);

        if (urlWasChanged &&
                monitorRepository.existsByUrlIgnoreCase(normalizedUrl)) {

            throw new DuplicateMonitorException(normalizedUrl);
        }

        monitor.setName(request.getName().trim());
        monitor.setUrl(normalizedUrl);

        if (request.getExpectedStatusCode() != null) {
            monitor.setExpectedStatusCode(
                    request.getExpectedStatusCode()
            );
        }

        if (request.getCheckIntervalSeconds() != null) {
            monitor.setCheckIntervalSeconds(
                    request.getCheckIntervalSeconds()
            );
        }

        if (request.getActive() != null) {
            monitor.setActive(request.getActive());
        }

        Monitor updatedMonitor = monitorRepository.save(monitor);

        return mapToResponse(updatedMonitor);
    }

    @Transactional
    public void deleteMonitor(Long id) {

        Monitor monitor = findMonitor(id);

        monitorRepository.delete(monitor);
    }

    private Monitor findMonitor(Long id) {

        return monitorRepository
                .findById(id)
                .orElseThrow(() ->
                        new MonitorNotFoundException(id)
                );
    }

    private String validateAndNormalizeUrl(String url) {

        try {
            URI uri = new URI(url.trim());

            String scheme = uri.getScheme();

            boolean supportedScheme =
                    "http".equalsIgnoreCase(scheme) ||
                            "https".equalsIgnoreCase(scheme);

            if (!supportedScheme || uri.getHost() == null) {
                throw new InvalidUrlException(url);
            }

            return uri.normalize().toString();

        } catch (URISyntaxException exception) {
            throw new InvalidUrlException(url);
        }
    }

    private MonitorResponse mapToResponse(Monitor monitor) {

        return MonitorResponse.builder()
                .id(monitor.getId())
                .name(monitor.getName())
                .url(monitor.getUrl())
                .expectedStatusCode(monitor.getExpectedStatusCode())
                .checkIntervalSeconds(
                        monitor.getCheckIntervalSeconds()
                )
                .active(monitor.getActive())
                .currentStatus(monitor.getCurrentStatus())
                .lastStatusCode(monitor.getLastStatusCode())
                .lastResponseTimeMs(
                        monitor.getLastResponseTimeMs()
                )
                .lastCheckedAt(monitor.getLastCheckedAt())
                .createdAt(monitor.getCreatedAt())
                .updatedAt(monitor.getUpdatedAt())
                .build();
    }
}