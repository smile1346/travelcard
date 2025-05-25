package com.example.travelcard.Entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "quotation")
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long custid;

    @Column(name = "exchange_rate_id", columnDefinition = "uuid")
    private UUID exchangeRateId;

    @Column(name = "quotation_type")
    private String quotationType;

    @Column(name = "source_currency")
    private String sourceCurrency;

    @Column(name = "dest_currency")
    private String destCurrency;

    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Constructors
    public Quotation() {}

    // Getters and setters

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustid() {
        return custid;
    }
    public void setCustid(Long custid) {
        this.custid = custid;
    }

    public UUID getExchangeRateId() {
        return exchangeRateId;
    }
    public void setExchangeRateId(UUID exchangeRateId) {
        this.exchangeRateId = exchangeRateId;
    }

    public String getQuotationType() {
        return quotationType;
    }
    public void setQuotationType(String quotationType) {
        this.quotationType = quotationType;
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

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}



