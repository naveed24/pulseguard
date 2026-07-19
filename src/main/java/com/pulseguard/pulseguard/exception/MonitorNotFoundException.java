package com.pulseguard.pulseguard.exception;

public class MonitorNotFoundException extends RuntimeException {

    public MonitorNotFoundException(Long id) {
        super("Monitor not found with id: " + id);
    }
}
