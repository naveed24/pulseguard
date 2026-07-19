package com.pulseguard.pulseguard.service;


import com.pulseguard.pulseguard.dto.MonitorResultResponse;
import com.pulseguard.pulseguard.entity.MonitorResult;
import com.pulseguard.pulseguard.exception.MonitorNotFoundException;
import com.pulseguard.pulseguard.repository.MonitorRepository;
import com.pulseguard.pulseguard.repository.MonitorResultRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitorHistoryService {
    private final MonitorRepository monitorRepository;
    private final MonitorResultRepository monitorResultRepository;

    @Transactional(readOnly = true)
    public List<MonitorResultResponse> getMonitorHistory(
            Long monitorId) {

        if (!monitorRepository.existsById(monitorId)) {
            throw new MonitorNotFoundException(monitorId);
        }

        return monitorResultRepository
                .findByMonitorIdOrderByCheckedAtDesc(monitorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private MonitorResultResponse mapToResponse(
            MonitorResult result) {

        return MonitorResultResponse.builder()
                .id(result.getId())
                .monitorId(result.getMonitor().getId())
                .monitorName(result.getMonitor().getName())
                .status(result.getStatus())
                .expectedStatusCode(
                        result.getExpectedStatusCode()
                )
                .actualStatusCode(
                        result.getActualStatusCode()
                )
                .responseTimeMs(
                        result.getResponseTimeMs()
                )
                .errorMessage(result.getErrorMessage())
                .checkedAt(result.getCheckedAt())
                .build();
    }

}
