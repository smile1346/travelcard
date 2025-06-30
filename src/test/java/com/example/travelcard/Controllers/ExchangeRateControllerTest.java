package com.example.travelcard.Controllers;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.travelcard.DTOs.ExchangeRateRequest;
import com.example.travelcard.Services.ExchangeRateService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ExchangeRateController.class)
public class ExchangeRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ExchangeRateService exchangeRateService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<ExchangeRateRequest> mockRequestList;

    @BeforeEach
    public void setUp() throws Exception {
        String json = """
            [
              {
                "effectiveDateTime": "2025-06-06T10:00:00",
                "announcementDate": "2025-05-06",
                "round": 1,
                "exchangeRates": [
                  {
                    "currencyCode": "USD",
                    "buyingRate": { "rate": 35.0, "perUnit": 1 },
                    "sellingRate": { "rate": 36.0, "perUnit": 1 },
                    "displayDecimal": 2
                  }
                ]
              }
            ]
        """;

        mockRequestList = objectMapper.readValue(json, new TypeReference<>() {});
    }

    @Test
    public void testUploadExchangeRates_success() throws Exception {
        Mockito.when(exchangeRateService.saveExchangeRateAndQuotations(any()))
               .thenReturn(UUID.randomUUID());

        String jsonContent = objectMapper.writeValueAsString(mockRequestList);

        MockMultipartFile file = new MockMultipartFile(
            "file", "rates.json", MediaType.APPLICATION_JSON_VALUE,
            jsonContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/exchange-rates/upload").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("SUCCESS"));
    }

    @Test
    public void testUploadExchangeRates_invalidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "invalid.json", MediaType.APPLICATION_JSON_VALUE,
            "not valid json".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/exchange-rates/upload").file(file))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Invalid file or format"));
    }

    @Test
    public void testUploadExchangeRates_partialFailure() throws Exception {
        Mockito.when(exchangeRateService.saveExchangeRateAndQuotations(any()))
               .thenThrow(new IllegalStateException("Outdated timestamp"));

        String jsonContent = objectMapper.writeValueAsString(mockRequestList);

        MockMultipartFile file = new MockMultipartFile(
            "file", "rates.json", MediaType.APPLICATION_JSON_VALUE,
            jsonContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/exchange-rates/upload").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("FAILED"))
            .andExpect(jsonPath("$[0].reason").value("Outdated timestamp"));
    }

    @Test
    public void testUploadExchangeRates_missingFields() throws Exception {
        String json = """
            [
              {
                "announcementDate": "2025-05-06",
                "round": 1,
                "exchangeRates": []
              }
            ]
        """;

        List<ExchangeRateRequest> badRequestList = objectMapper.readValue(json, new TypeReference<>() {});
        String jsonContent = objectMapper.writeValueAsString(badRequestList);

        Mockito.when(exchangeRateService.saveExchangeRateAndQuotations(any()))
                .thenThrow(new IllegalArgumentException("Invalid format: Missing or invalid required fields."));

        MockMultipartFile file = new MockMultipartFile("file", "rates.json", MediaType.APPLICATION_JSON_VALUE, jsonContent.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/exchange-rates/upload").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("FAILED"))
            .andExpect(jsonPath("$[0].reason").value("Invalid format: Missing or invalid required fields."));
    }

    @Test
    public void testUploadExchangeRates_unquotedCurrency() throws Exception {
        String json = """
            [
              {
                "effectiveDateTime": "2025-06-06T10:00:00",
                "announcementDate": "2025-05-06",
                "round": 1,
                "exchangeRates": [
                  {
                    "currencyCode": null,
                    "buyingRate": null,
                    "sellingRate": { "rate": 36.0, "perUnit": 1 },
                    "displayDecimal": 2
                  }
                ]
              }
            ]
        """;

        List<ExchangeRateRequest> badCurrencyList = objectMapper.readValue(json, new TypeReference<>() {});
        String jsonContent = objectMapper.writeValueAsString(badCurrencyList);

        Mockito.when(exchangeRateService.saveExchangeRateAndQuotations(any()))
                .thenThrow(new IllegalArgumentException("Unquoted or unsupported currency in payload."));

        MockMultipartFile file = new MockMultipartFile("file", "rates.json", MediaType.APPLICATION_JSON_VALUE, jsonContent.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/exchange-rates/upload").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("FAILED"))
            .andExpect(jsonPath("$[0].reason").value("Unquoted or unsupported currency in payload."));
    }

    @Test
    public void testUploadExchangeRates_duplicateRequest() throws Exception {
        Mockito.when(exchangeRateService.saveExchangeRateAndQuotations(any()))
                .thenThrow(new IllegalStateException("Duplicate request for same round and date"));

        String jsonContent = objectMapper.writeValueAsString(mockRequestList);
        MockMultipartFile file = new MockMultipartFile("file", "rates.json", MediaType.APPLICATION_JSON_VALUE, jsonContent.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/exchange-rates/upload").file(file))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status").value("FAILED"))
            .andExpect(jsonPath("$[0].reason").value("Duplicate request for same round and date"));
    }
}