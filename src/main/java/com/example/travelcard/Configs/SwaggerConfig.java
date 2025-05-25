package com.example.travelcard.Configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI travelcardOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Travel Card Exchange Rate API")
                .description("API for uploading and retrieving exchange rate data")
                .version("1.0"));
    }
}

