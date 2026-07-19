package com.pulseguard.pulseguard.service;


import com.pulseguard.pulseguard.dto.MonitorStatisticsResponse;
import com.pulseguard.pulseguard.entity.Monitor;
import com.pulseguard.pulseguard.entity.MonitorResult;
import com.pulseguard.pulseguard.enums.MonitorStatus;
import com.pulseguard.pulseguard.exception.MonitorNotFoundException;
import com.pulseguard.pulseguard.repository.MonitorRepository;
import com.pulseguard.pulseguard.repository.MonitorResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonitorStatisticsServiceTest {

    @Mock
    private MonitorRepository monitorRepository;

    @Mock
    private MonitorResultRepository monitorResultRepository;

    @InjectMocks
    private MonitorStatisticsService monitorStatisticsService;

    @Test
    void shouldCalculateMonitorStatistics() {

        Monitor monitor = new Monitor();
        monitor.setId(1L);
        monitor.setName("Google");
        monitor.setUrl("https://www.google.com");
        monitor.setCurrentStatus(MonitorStatus.UP);

        LocalDateTime currentTime = LocalDateTime.now();

        MonitorResult firstResult = createResult(
                monitor,
                MonitorStatus.UP,
                200L,
                currentTime
        );

        MonitorResult secondResult = createResult(
                monitor,
                MonitorStatus.UP,
                300L,
                currentTime.minusMinutes(1)
        );

        MonitorResult thirdResult = createResult(
                monitor,
                MonitorStatus.DOWN,
                400L,
                currentTime.minusMinutes(2)
        );

        when(monitorRepository.findById(1L))
                .thenReturn(Optional.of(monitor));

        when(
                monitorResultRepository
                        .findByMonitorIdOrderByCheckedAtDesc(1L)
        ).thenReturn(
                List.of(
                        firstResult,
                        secondResult,
                        thirdResult
                )
        );

        MonitorStatisticsResponse response =
                monitorStatisticsService.getStatistics(1L);

        assertEquals(1L, response.getMonitorId());
        assertEquals("Google", response.getMonitorName());

        assertEquals(3, response.getTotalChecks());
        assertEquals(2, response.getSuccessfulChecks());
        assertEquals(1, response.getFailedChecks());

        assertEquals(
                66.67,
                response.getUptimePercentage(),
                0.01
        );

        assertEquals(
                300.0,
                response.getAverageResponseTimeMs(),
                0.01
        );

        assertEquals(200L, response.getMinimumResponseTimeMs());
        assertEquals(400L, response.getMaximumResponseTimeMs());

        verify(monitorRepository).findById(1L);

        verify(monitorResultRepository)
                .findByMonitorIdOrderByCheckedAtDesc(1L);
    }

    @Test
    void shouldThrowExceptionWhenMonitorDoesNotExist() {

        when(monitorRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                MonitorNotFoundException.class,
                () -> monitorStatisticsService.getStatistics(99L)
        );

        verify(
                monitorResultRepository,
                never()
        ).findByMonitorIdOrderByCheckedAtDesc(anyLong());
    }

    private MonitorResult createResult(
            Monitor monitor,
            MonitorStatus status,
            Long responseTimeMs,
            LocalDateTime checkedAt) {

        MonitorResult result = new MonitorResult();

        result.setMonitor(monitor);
        result.setStatus(status);
        result.setExpectedStatusCode(200);
        result.setActualStatusCode(
                status == MonitorStatus.UP ? 200 : 500
        );
        result.setResponseTimeMs(responseTimeMs);
        result.setCheckedAt(checkedAt);

        return result;
    }
}