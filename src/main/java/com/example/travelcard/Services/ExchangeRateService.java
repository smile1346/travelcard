package com.example.travelcard.Services;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.travelcard.DTOs.ExchangeRateRequest;
import com.example.travelcard.DTOs.ExchangeRateRequest.ExchangeRateDetail;
import com.example.travelcard.DTOs.ExchangeRateWithQuotationsResponse;
import com.example.travelcard.Repositories.ExchangeRateRepository;
import com.example.travelcard.Repositories.QuotationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class ExchangeRateService {

    private final EntityManager entityManager;
    private final QuotationRepository quotationRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public ExchangeRateService(EntityManager entityManager,
                               QuotationRepository quotationRepository,
                               StringRedisTemplate redisTemplate,
                               ObjectMapper objectMapper,
                               ExchangeRateRepository exchangeRateRepository) {
        this.entityManager = entityManager;
        this.quotationRepository = quotationRepository;
        this.exchangeRateRepository = exchangeRateRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

@Transactional
public UUID saveExchangeRateAndQuotations(ExchangeRateRequest req) throws JsonProcessingException {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime effectiveDateTime = req.getEffectiveDateTime();

    if (req.getRound() <= 0 || req.getExchangeRates() == null || req.getExchangeRates().isEmpty()) {
        throw new IllegalArgumentException("Invalid format: Missing or invalid required fields.");
    }

    String yyyymm = getTwoMonthPeriodKey(effectiveDateTime);
    String tableName = "exchange_rates_" + yyyymm;
    createTableIfNotExists(tableName);

    for (ExchangeRateDetail rate : req.getExchangeRates()) {
        if (rate.getCurrencyCode() == null || rate.getBuyingRate() == null) {
            throw new IllegalArgumentException("Unquoted or unsupported currency in payload.");
        }
    }

    String jsonRates = objectMapper.writeValueAsString(req.getExchangeRates());

    List<?> existingIds = entityManager.createNativeQuery(
            String.format("SELECT id FROM %s WHERE round = :round AND announcement_date = :announcementDate", tableName))
        .setParameter("round", req.getRound())
        .setParameter("announcementDate", req.getAnnouncementDate())
        .getResultList();

    UUID exchangeRateId;

    if (!existingIds.isEmpty()) {
        exchangeRateId = (UUID) existingIds.get(0);

        String updateSql = String.format("""
            UPDATE %s SET 
                effective_date_time = :effectiveDateTime,
                created_at = :createdAt,
                rates = cast(:rates as jsonb)
            WHERE round = :round AND announcement_date = :announcementDate
        """, tableName);

        entityManager.createNativeQuery(updateSql)
            .setParameter("effectiveDateTime", effectiveDateTime)
            .setParameter("createdAt", now)
            .setParameter("rates", jsonRates)
            .setParameter("round", req.getRound())
            .setParameter("announcementDate", req.getAnnouncementDate())
            .executeUpdate();
    } else {
        exchangeRateId = UUID.randomUUID();

        String insertSql = String.format("""
            INSERT INTO %s (id, created_at, effective_date_time, announcement_date, round, rates)
            VALUES (:id, :createdAt, :effectiveDateTime, :announcementDate, :round, cast(:rates as jsonb))
        """, tableName);

        entityManager.createNativeQuery(insertSql)
            .setParameter("id", exchangeRateId)
            .setParameter("createdAt", now)
            .setParameter("effectiveDateTime", effectiveDateTime)
            .setParameter("announcementDate", req.getAnnouncementDate())
            .setParameter("round", req.getRound())
            .setParameter("rates", jsonRates)
            .executeUpdate();
    }

    cacheExchangeRateInRedis(yyyymm, jsonRates);
    return exchangeRateId;
}

   public ExchangeRateWithQuotationsResponse getExchangeRateById(UUID exchangeRateId) {
    String yyyymm = findTablePeriodContainingExchangeRate(exchangeRateId);
    if (yyyymm == null) {
        throw new RuntimeException("ExchangeRate not found in any dynamic table.");
    }

    String tableName = "exchange_rates_" + yyyymm;

    String sql = String.format("""
        SELECT id, created_at, effective_date_time, announcement_date, round, rates
        FROM %s WHERE id = :id
    """, tableName);

    Object[] row = (Object[]) entityManager.createNativeQuery(sql)
        .setParameter("id", exchangeRateId)
        .getSingleResult();

    ExchangeRateWithQuotationsResponse response = new ExchangeRateWithQuotationsResponse();
    response.setId((UUID) row[0]);
    response.setCreatedAt(((Timestamp) row[1]).toLocalDateTime());
    response.setEffectiveDateTime(((Timestamp) row[2]).toLocalDateTime());
    response.setAnnouncementDate(((Date) row[3]).toLocalDate());
    response.setRound((Integer) row[4]);
    // 👇 Parse the JSONB rates column into list of ExchangeRateDetail
    String ratesJson = row[5].toString();
    try {
        List<ExchangeRateDetail> exchangeRates = objectMapper.readValue(
            ratesJson,
            new TypeReference<List<ExchangeRateRequest.ExchangeRateDetail>>() {}
        );
        response.setExchangeRates(exchangeRates); // Ensure your response DTO has this field
        response.setExchangeRates(exchangeRates); // Ensure your response DTO has this field
    } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to parse exchange rates JSON", e);
    }

    return response;
}

private String findTablePeriodContainingExchangeRate(UUID exchangeRateId) {
    List<String> knownTables = List.of("202405", "202503", "202505"); // Update dynamically if needed

    for (String yyyymm : knownTables) {
        String tableName = "exchange_rates_" + yyyymm;
        try {
            Long count = ((Number) entityManager.createNativeQuery(
                    String.format("SELECT COUNT(*) FROM %s WHERE id = :id", tableName))
                .setParameter("id", exchangeRateId)
                .getSingleResult()).longValue();

            if (count > 0) {
                return yyyymm;
            }
        } catch (Exception ignored) {
        }
    }
    return null;
}

    private void cacheExchangeRateInRedis(String yyyymm, String jsonRates) {
        String key = "exchange_rates:" + yyyymm;
        redisTemplate.opsForValue().set(key, jsonRates);
    }

    private String getTwoMonthPeriodKey(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int adjustedMonth = ((month - 1) / 2) * 2 + 1;
        return String.format("%04d%02d", year, adjustedMonth);
    }

    private void createTableIfNotExists(String tableName) {
        String sql = String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                id UUID PRIMARY KEY,
                created_at TIMESTAMP,
                effective_date_time TIMESTAMP,
                announcement_date DATE,
                round INT,
                rates JSONB
            )
        """, tableName);

        entityManager.createNativeQuery(sql).executeUpdate();
    }
}