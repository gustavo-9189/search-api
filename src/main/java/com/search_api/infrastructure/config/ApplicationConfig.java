package com.search_api.infrastructure.config;

import com.search_api.application.service.CountSearchService;
import com.search_api.application.service.CreateSearchService;
import com.search_api.domain.port.in.CountSearchUseCase;
import com.search_api.domain.port.in.CreateSearchUseCase;
import com.search_api.domain.port.out.SearchEventPublisher;
import com.search_api.domain.port.out.SearchRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public CreateSearchUseCase createSearchUseCase(SearchEventPublisher publisher) {
        return new CreateSearchService(publisher);
    }

    @Bean
    public CountSearchUseCase countSearchUseCase(SearchRepository repository) {
        return new CountSearchService(repository);
    }
}