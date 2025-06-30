package com.example.travelcard.Controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.travelcard.DTOs.ExchangeRateRequest;
import com.example.travelcard.DTOs.ExchangeRateWithQuotationsResponse;
import com.example.travelcard.Services.ExchangeRateService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/exchange-rates")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadExchangeRate(@RequestBody ExchangeRateRequest req) {
        Map<String, Object> response = new HashMap<>();
        response.put("announcementDate", req.getAnnouncementDate());
        response.put("round", req.getRound());

        try {
            UUID id = exchangeRateService.saveExchangeRateAndQuotations(req);
            response.put("status", "SUCCESS");
            response.put("exchangeRateId", id);
        } catch (IllegalArgumentException | IllegalStateException e) {
            response.put("status", "FAILED");
            response.put("errorType", "Validation");
            response.put("reason", e.getMessage());
        } catch (JsonProcessingException e) {
            response.put("status", "FAILED");
            response.put("errorType", "Serialization");
            response.put("reason", "Invalid JSON processing: " + e.getMessage());
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("errorType", "Server");
            response.put("reason", "Unexpected error: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    // @GetMapping("/{id}/quotations")
    // public ResponseEntity<List<Quotation>> getQuotationsForExchangeRate(@PathVariable UUID id) {
    //     return ResponseEntity.ok(exchangeRateService.getQuotationsForExchangeRate(id));
    // }
@GetMapping("/{id}/only")
public ResponseEntity<ExchangeRateWithQuotationsResponse> getExchangeRateOnly(@PathVariable UUID id) {
    return ResponseEntity.ok(exchangeRateService.getExchangeRateById(id));
}

}