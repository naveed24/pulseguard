package com.pulseguard.pulseguard.exception;

public class InvalidUrlException extends RuntimeException {

    public InvalidUrlException(String url) {
        super("Invalid website URL: " + url);
    }
}