package com.example.travelcard.Exceptions;

public class DuplicateRateException extends RuntimeException {
    public DuplicateRateException(String message) {
        super(message);
    }
}