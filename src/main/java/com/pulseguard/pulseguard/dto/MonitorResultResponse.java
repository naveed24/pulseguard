package com.pulseguard.pulseguard.dto;


import com.pulseguard.pulseguard.enums.MonitorStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MonitorResultResponse {

    private Long id;
    private Long monitorId;
    private String monitorName;
    private MonitorStatus status;
    private Integer expectedStatusCode;
    private Integer actualStatusCode;
    private Long responseTimeMs;
    private String errorMessage;
    private LocalDateTime checkedAt;
}
