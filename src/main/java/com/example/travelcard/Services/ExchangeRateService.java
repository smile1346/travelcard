package com.example.travelcard.Services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.travelcard.DTOs.ExchangeRateRequest;
import com.example.travelcard.Entities.Quotation;
import com.example.travelcard.Repositories.QuotationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class ExchangeRateService {

    private final EntityManager entityManager;
    private final QuotationRepository quotationRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ExchangeRateService(EntityManager entityManager,
                               QuotationRepository quotationRepository,
                               StringRedisTemplate redisTemplate,
                               ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.quotationRepository = quotationRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UUID saveExchangeRateAndQuotations(ExchangeRateRequest req) throws JsonProcessingException {
    UUID exchangeRateId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    LocalDateTime effectiveDateTime = req.getEffectiveDateTime();
    String yyyymm = getTwoMonthPeriodKey(effectiveDateTime);
    String tableName = "exchange_rates_" + yyyymm;

    // Ensure the table exists
    createTableIfNotExists(tableName);

    String jsonRates = objectMapper.writeValueAsString(req.getExchangeRates());

    // Insert into exchange_rates_yyyymm table
    String sql = """
        INSERT INTO %s (id, created_at, effective_date_time, announcement_date, round, rates)
        VALUES (:id, :createdAt, :effectiveDateTime, :announcementDate, :round, cast(:rates as jsonb))
    """.formatted(tableName);

    entityManager.createNativeQuery(sql)
        .setParameter("id", exchangeRateId)
        .setParameter("createdAt", now)
        .setParameter("effectiveDateTime", effectiveDateTime)
        .setParameter("announcementDate", req.getAnnouncementDate())
        .setParameter("round", req.getRound())
        .setParameter("rates", jsonRates)
        .executeUpdate();

    // Save individual Quotation entries
    for (ExchangeRateRequest.ExchangeRateDetail rate : req.getExchangeRates()) {
        Quotation quotation = new Quotation();
        quotation.setCustid(null);  // Update if needed
        quotation.setExchangeRateId(exchangeRateId);
        quotation.setQuotationType("DEFAULT");
        quotation.setSourceCurrency(rate.getCurrencyCode());
        quotation.setDestCurrency("LOCAL");
        quotation.setExchangeRate(BigDecimal.valueOf(rate.getBuyingRate().getRate()));
        quotation.setCreatedAt(now);
        quotation.setExpiresAt(null);

        quotationRepository.save(quotation);
    }

    cacheExchangeRateInRedis(yyyymm, jsonRates);
    return exchangeRateId;
}

private void cacheExchangeRateInRedis(String yyyymm, String jsonRates) {
    String key = "exchange_rates:" + yyyymm;
    redisTemplate.opsForValue().set(key, jsonRates);
}

private String getTwoMonthPeriodKey(LocalDateTime dateTime) {
    int year = dateTime.getYear();
    int month = dateTime.getMonthValue();

    // Adjust to the 2-month period starting month (1, 3, 5, 7, 9, 11)
    int adjustedMonth = ((month - 1) / 2) * 2 + 1;

    return String.format("%04d%02d", year, adjustedMonth);
}

private void createTableIfNotExists(String tableName) {
    String sql = """
        CREATE TABLE IF NOT EXISTS %s (
            id UUID PRIMARY KEY,
            created_at TIMESTAMP,
            effective_date_time TIMESTAMP,
            announcement_date DATE,
            round INT,
            rates JSONB
        )
    """.formatted(tableName);

    entityManager.createNativeQuery(sql).executeUpdate();
}


}
