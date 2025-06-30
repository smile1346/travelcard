package com.example.travelcard.DTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.travelcard.DTOs.ExchangeRateRequest.ExchangeRateDetail;

public class ExchangeRateWithQuotationsResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime effectiveDateTime;
    private LocalDate announcementDate;
    private int round;
    private List<QuotationDTO> quotations;

    private List<ExchangeRateDetail> exchangeRates;

    public List<ExchangeRateDetail> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(List<ExchangeRateDetail> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }
    
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
    public List<QuotationDTO> getQuotations() {
        return quotations;
    }
    public void setQuotations(List<QuotationDTO> quotations) {
        this.quotations = quotations;
    }

    // Getters, setters, constructor
}
