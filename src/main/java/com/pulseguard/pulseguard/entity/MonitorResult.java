package com.pulseguard.pulseguard.entity;


import com.pulseguard.pulseguard.enums.MonitorStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "monitor_results",
        indexes = {
                @Index(
                        name = "idx_monitor_result_monitor_id",
                        columnList = "monitor_id"
                ),
                @Index(
                        name = "idx_monitor_result_checked_at",
                        columnList = "checked_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class MonitorResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "monitor_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_monitor_result_monitor"
            )
    )
    private Monitor monitor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MonitorStatus status;

    @Column(name = "expected_status_code", nullable = false)
    private Integer expectedStatusCode;

    @Column(name = "actual_status_code")
    private Integer actualStatusCode;

    @Column(name = "response_time_ms", nullable = false)
    private Long responseTimeMs;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "checked_at", nullable = false)
    private LocalDateTime checkedAt;

    @PrePersist
    public void prePersist() {
        if (checkedAt == null) {
            checkedAt = LocalDateTime.now();
        }
    }
}
