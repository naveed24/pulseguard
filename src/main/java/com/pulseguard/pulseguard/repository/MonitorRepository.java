package com.pulseguard.pulseguard.repository;

import com.pulseguard.pulseguard.entity.Monitor;
import com.pulseguard.pulseguard.enums.MonitorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    boolean existsByUrlIgnoreCase(String url);

    Optional<Monitor> findByUrlIgnoreCase(String url);

    List<Monitor> findByActiveTrue();

    List<Monitor> findByCurrentStatus(MonitorStatus currentStatus);

    List<Monitor> findByActiveTrueOrderByNameAsc();
}
