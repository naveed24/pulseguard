package com.pulseguard.pulseguard.dto;


import com.pulseguard.pulseguard.enums.MonitorStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MonitorStatisticsResponse {

    private Long monitorId;
    private String monitorName;
    private String url;
    private MonitorStatus currentStatus;

    private long totalChecks;
    private long successfulChecks;
    private long failedChecks;

    private double uptimePercentage;
    private double averageResponseTimeMs;

    private Long minimumResponseTimeMs;
    private Long maximumResponseTimeMs;

    private LocalDateTime lastCheckedAt;
}
