package com.pulseguard.pulseguard.dto;

import com.pulseguard.pulseguard.enums.MonitorStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MonitorResponse {

    private Long id;
    private String name;
    private String url;
    private Integer expectedStatusCode;
    private Integer checkIntervalSeconds;
    private Boolean active;
    private MonitorStatus currentStatus;
    private Integer lastStatusCode;
    private Long lastResponseTimeMs;
    private LocalDateTime lastCheckedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
