package com.example.travelcard.Entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ExchangeRate {

    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime effectiveDateTime;
    private LocalDate announcementDate;
    private int round;
    private String rates;  // JSON stored as String

    // Constructors
    public ExchangeRate() {}

    public ExchangeRate(UUID id, LocalDateTime createdAt, LocalDateTime effectiveDateTime, LocalDate announcementDate, int round, String rates) {
        this.id = id;
        this.createdAt = createdAt;
        this.effectiveDateTime = effectiveDateTime;
        this.announcementDate = announcementDate;
        this.round = round;
        this.rates = rates;
    }

    // Getters and setters

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getEffectiveDateTime() {
        return effectiveDateTime;
    }
    public void setEffectiveDateTime(LocalDateTime effectiveDateTime) {
        this.effectiveDateTime = effectiveDateTime;
    }
    public LocalDate getAnnouncementDate() {
        return announcementDate;
    }
    public void setAnnouncementDate(LocalDate announcementDate) {
        this.announcementDate = announcementDate;
    }
    public int getRound() {
        return round;
    }
    public void setRound(int round) {
        this.round = round;
    }
    public String getRates() {
        return rates;
    }
    public void setRates(String rates) {
        this.rates = rates;
    }
}

