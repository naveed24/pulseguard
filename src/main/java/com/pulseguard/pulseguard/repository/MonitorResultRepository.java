package com.pulseguard.pulseguard.repository;

import com.pulseguard.pulseguard.entity.MonitorResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitorResultRepository
        extends JpaRepository<MonitorResult, Long> {

    List<MonitorResult>
    findByMonitorIdOrderByCheckedAtDesc(Long monitorId);

    List<MonitorResult>
    findTop20ByMonitorIdOrderByCheckedAtDesc(Long monitorId);

    long countByMonitorId(Long monitorId);
}
