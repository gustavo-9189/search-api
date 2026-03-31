package com.search_api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI searchApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Search API")
                        .description("Servicio de búsqueda.")
                        .version("1.0.0")
                        .contact(new Contact().name("Gustavo Martínez")));
    }
}
