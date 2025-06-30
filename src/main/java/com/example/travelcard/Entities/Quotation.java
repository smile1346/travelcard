package com.example.travelcard.Entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "quotation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "custid", nullable = false)
    private Long custId;

    @Column(name = "exchange_rate_id", nullable = false)
    private java.util.UUID exchangeRateId;

    @Column(name = "quotation_type", nullable = false)
    private String quotationType; // e.g. BUY, SELL

    @Column(name = "source_currency", nullable = false)
    private String sourceCurrency;

    @Column(name = "dest_currency", nullable = false)
    private String destCurrency;

    @Column(name = "exchange_rate", nullable = false, precision = 19, scale = 4)
    private BigDecimal exchangeRate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}