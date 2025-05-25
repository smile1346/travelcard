package com.example.travelcard.DTOs;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExchangeRateRequest {

    @JsonProperty("effectiveDateTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime effectiveDateTime;

    @JsonProperty("announcementDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate announcementDate;

    @JsonProperty("round")
    private int round;

    @JsonProperty("exchangeRates")
    private List<ExchangeRateDetail> exchangeRates;

    // Getters & setters

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

    public List<ExchangeRateDetail> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(List<ExchangeRateDetail> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

    // Nested static class for exchange rate details
    public static class ExchangeRateDetail {

        @JsonProperty("currencyCode")
        private String currencyCode;

        @JsonProperty("buyingRate")
        private Rate buyingRate;

        @JsonProperty("sellingRate")
        private Rate sellingRate;

        @JsonProperty("displayDecimal")
        private int displayDecimal;

        // Getters & setters

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public Rate getBuyingRate() {
            return buyingRate;
        }

        public void setBuyingRate(Rate buyingRate) {
            this.buyingRate = buyingRate;
        }

        public Rate getSellingRate() {
            return sellingRate;
        }

        public void setSellingRate(Rate sellingRate) {
            this.sellingRate = sellingRate;
        }

        public int getDisplayDecimal() {
            return displayDecimal;
        }

        public void setDisplayDecimal(int displayDecimal) {
            this.displayDecimal = displayDecimal;
        }
    }

    public static class Rate {

        @JsonProperty("rate")
        private double rate;

        @JsonProperty("perUnit")
        private int perUnit;

        // Getters & setters

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }

        public int getPerUnit() {
            return perUnit;
        }

        public void setPerUnit(int perUnit) {
            this.perUnit = perUnit;
        }
    }
}