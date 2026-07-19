package com.pulseguard.pulseguard.exception;

public class DuplicateMonitorException extends RuntimeException {

    public DuplicateMonitorException(String url) {
        super("A monitor already exists for URL: " + url);
    }
}
