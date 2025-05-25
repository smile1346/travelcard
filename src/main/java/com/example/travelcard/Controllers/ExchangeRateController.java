package com.example.travelcard.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.travelcard.DTOs.ExchangeRateRequest;
import com.example.travelcard.Services.ExchangeRateService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;
    private final ObjectMapper objectMapper;

    public ExchangeRateController(ExchangeRateService exchangeRateService, ObjectMapper objectMapper) {
        this.exchangeRateService = exchangeRateService;
        this.objectMapper = objectMapper;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadExchangeRatesFile(@RequestParam("file") MultipartFile file) {
        try {
            // Parse the file as JSON array
            List<ExchangeRateRequest> requests = objectMapper.readValue(
                file.getInputStream(),
                new TypeReference<List<ExchangeRateRequest>>() {}
            );

            // Process each request
            for (ExchangeRateRequest request : requests) {
                exchangeRateService.saveExchangeRateAndQuotations(request);
            }

            return ResponseEntity.ok(Map.of("status", "success", "processed", requests.size()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Failed to process uploaded file: " + e.getMessage()));
        }
    }
}

