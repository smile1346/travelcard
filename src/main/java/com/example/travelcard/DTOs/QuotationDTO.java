package com.example.travelcard.DTOs;

public class QuotationDTO {
    private String sourceCurrency;
    private String destCurrency;
    private Double exchangeRate;

    public QuotationDTO() {}

    public QuotationDTO(String sourceCurrency, String destCurrency, Double exchangeRate) {
        this.sourceCurrency = sourceCurrency;
        this.destCurrency = destCurrency;
        this.exchangeRate = exchangeRate;
    }

    public String getSourceCurrency() {
        return sourceCurrency;
    }
    public void setSourceCurrency(String sourceCurrency) {
        this.sourceCurrency = sourceCurrency;
    }

    public String getDestCurrency() {
        return destCurrency;
    }
    public void setDestCurrency(String destCurrency) {
        this.destCurrency = destCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }
    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}

