package com.example.travelcard.Repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.travelcard.Entities.Quotation;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    
    // Example: Find all quotations by customer ID
    List<Quotation> findByCustid(Long custid);
    
    // Example: Find all quotations by exchange rate UUID
    List<Quotation> findByExchangeRateId(UUID exchangeRateId);

    @Query("SELECT MAX(q.expiresAt) FROM Quotation q")
    LocalDateTime findMaxExpiresAt();

    boolean existsBySourceCurrencyAndExchangeRate(String sourceCurrency, BigDecimal exchangeRate);
    
}
