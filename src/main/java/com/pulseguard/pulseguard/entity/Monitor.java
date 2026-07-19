package com.pulseguard.pulseguard.entity;


import com.pulseguard.pulseguard.enums.MonitorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "monitors",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_monitor_url",
                        columnNames = "url"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 2048)
    private String url;

    @Column(name = "expected_status_code", nullable = false)
    private Integer expectedStatusCode;

    @Column(name = "check_interval_seconds", nullable = false)
    private Integer checkIntervalSeconds;

    @Column(nullable = false)
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 20)
    private MonitorStatus currentStatus;

    @Column(name = "last_status_code")
    private Integer lastStatusCode;

    @Column(name = "last_response_time_ms")
    private Long lastResponseTimeMs;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime currentTime = LocalDateTime.now();

        createdAt = currentTime;
        updatedAt = currentTime;

        if (expectedStatusCode == null) {
            expectedStatusCode = 200;
        }

        if (checkIntervalSeconds == null) {
            checkIntervalSeconds = 60;
        }

        if (active == null) {
            active = true;
        }

        if (currentStatus == null) {
            currentStatus = MonitorStatus.UNKNOWN;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
