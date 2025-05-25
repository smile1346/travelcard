package com.example.travelcard.Exceptions;

public class TechnicalIssueException extends RuntimeException {
    public TechnicalIssueException(String message) {
        super(message);
    }
}