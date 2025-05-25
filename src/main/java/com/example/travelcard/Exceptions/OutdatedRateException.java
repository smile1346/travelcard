package com.example.travelcard.Exceptions;

public class OutdatedRateException extends RuntimeException {
    public OutdatedRateException(String message) {
        super(message);
    }
}